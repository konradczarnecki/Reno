package konra.reno.core;

import konra.reno.account.Account;
import konra.reno.core.callback.CallbackHandler;
import konra.reno.core.callback.CallbackType;
import konra.reno.core.persistance.BlockRepository;
import konra.reno.core.persistance.StateRepository;
import konra.reno.core.reward.RewardConfig;
import konra.reno.transaction.Transaction;
import konra.reno.transaction.TransactionPool;
import konra.reno.util.FileService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class CoreService {

    BlockRepository blockRepository;
    StateRepository stateRepository;
    RewardConfig rewardConfig;
    CoreConfig config;
    FileService fileService;
    CallbackHandler callbackHandler;
    ScheduledExecutorService exec;

    @Getter TransactionPool transactionPool;
    @Getter long headBlockId;
    @Getter @Setter long networkHead;

    @Autowired
    public CoreService(BlockRepository blockRepository,
                       StateRepository stateRepository,
                       RewardConfig rewardConfig,
                       CoreConfig config,
                       CallbackHandler callbackHandler,
                       FileService fileService) {

        this.fileService = fileService;
        this.blockRepository = blockRepository;
        this.stateRepository = stateRepository;
        this.rewardConfig = rewardConfig;
        this.callbackHandler = callbackHandler;
        this.config = config;
        this.transactionPool = new TransactionPool();
        exec  = Executors.newScheduledThreadPool(10);
    }

    @Transactional
    public boolean startBlockchain() {

        Block initialBlock = new Block(null);
        blockRepository.save(initialBlock);
        setHeadBlockId(initialBlock.getId());

        return true;
    }

    public void setHeadBlockId(long id) {

        headBlockId = id;
        callbackHandler.execute(CallbackType.HEAD_EXCHANGE);
    }

    public List<Block> getBlocks(long fromId, long toId) {

        return blockRepository.findBlocksByIdBetween(fromId, toId);
    }

    public void processIncomingBlocks(List<Block> incomingBlocks) {

        incomingBlocks.forEach(this::processBlock);
    }

    private void processBlock(Block block) {

        if(verifyBlock(block)) acceptBlock(block);
    }

    private boolean verifyBlock(Block block) {

        Block currentHead = blockRepository.findTopByOrderByIdDesc();

        return block.getId() - 1 == currentHead.getId() &&
                Block.validate(block, currentHead.getPreviousPOW()) &&
                verifyTransactions(block);
    }

    private boolean verifyTransactions(Block block) {

        List<Transaction> transactions = block.getTransactions();
        if(!transactions.stream().allMatch(Transaction::validate)) return false;

        Map<String, Double> summedMap = getSummedTransactionsMap(transactions);

        for(Map.Entry<String, Double> entry: summedMap.entrySet()) {

            Account acc = stateRepository.findAccountByAddress(entry.getKey());
            if(acc == null || acc.getBalance() < entry.getValue()) return false;
        }

        return checkMinerTransaction(block);
    }

    private Map<String, Double> getSummedTransactionsMap(List<Transaction> transactions) {

        Map<String, Double> summedMap = new HashMap<>();

        transactions.stream().filter(transaction -> !transaction.getSender().equals(config.getSourceAccount())).forEach(transaction -> {

            String sender = transaction.getSender();

            if(summedMap.containsKey(sender)) summedMap.put(sender, summedMap.get(sender) + transaction.getAmount());
            else summedMap.put(sender, 0d);
        });

        return summedMap;
    }

    private boolean checkMinerTransaction(Block block) {

        List<Transaction> minerTransactionList = block.getTransactions().stream()
                .filter(transaction -> transaction.getSender().equals(config.getSourceAccount()) && transaction.getAmount() != 0)
                .collect(Collectors.toList());

        if (minerTransactionList.size() != 1) return false;

        Transaction minerTransaction = minerTransactionList.get(0);

        return minerTransaction.getSender().equals(block.getMiner()) &&
                !(minerTransaction.getAmount() != rewardConfig.getReward(block));
    }

    @Transactional
    protected void acceptBlock(Block block) {

        blockRepository.save(block);
        block.getTransactions().forEach(this::applyTransaction);
        headBlockId = block.getId();

        if(!stateRepository.getCollectionHash("state").equals(block.getStateHash()))
            throw new RuntimeException("Verified block state hash mismatch.");
    }

    private void applyTransaction(Transaction transaction) {

        if(transaction.getSender().equals(config.getSourceAccount())) {

            if(transaction.getAmount() == 0) applyNewAccountTransaction(transaction.getReceiver());
            else applyMinerTransaction(transaction.getReceiver(), transaction.getAmount());

        } else applyTransferTransaction(transaction);
    }

    @Transactional
    protected void applyTransferTransaction(Transaction transaction) {

        Account sender = stateRepository.findAccountByAddress(transaction.getSender());
        Account receiver = stateRepository.findAccountByAddress(transaction.getReceiver());

        sender.subtract(transaction.getAmount());
        receiver.add(transaction.getAmount());
    }

    @Transactional
    protected void applyNewAccountTransaction(String address) {

        Account acc = new Account(address);
        stateRepository.save(acc);
    }

    @Transactional
    protected void applyMinerTransaction(String minerAddress, Double amount) {

        Account miner = stateRepository.findAccountByAddress(minerAddress);
        miner.add(amount);
    }

    public boolean rollbackLastBlock() {
        return rollbackFromBlock(headBlockId);
    }

    public boolean rollbackFromBlock(long blockId) {
        return true;
    }

    public void addNewTransaction(Transaction transaction) {

        transactionPool.addPending(transaction);
        callbackHandler.execute(CallbackType.TRANSACTION, transaction);
    }
}
