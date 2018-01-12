package konra.reno.core;

import konra.reno.account.Account;
import konra.reno.core.callback.CallbackHandler;
import konra.reno.core.callback.CallbackType;
import konra.reno.core.persistance.BlockRepository;
import konra.reno.core.persistance.StateRepository;
import konra.reno.core.reward.RewardConfig;
import konra.reno.transaction.Transaction;
import konra.reno.transaction.TransactionPool;
import konra.reno.transaction.TransactionService;
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

        Map<String, Double> summedMap = TransactionService.getSummedTxMap(transactions);

        for(Map.Entry<String, Double> entry: summedMap.entrySet()) {

            Account acc = stateRepository.findAccountByAddress(entry.getKey());
            if(acc == null || acc.getBalance() < entry.getValue()) return false;
        }

        return true;
    }

    @Transactional
    protected void acceptBlock(Block block) {

        blockRepository.save(block);

        Account miner = stateRepository.findAccountByAddress(block.getMiner());
        if(miner == null) miner = new Account(block.getMiner());

        miner.add(rewardConfig.getReward(block));
        miner.add(TransactionService.collectFees(block.getTransactions()));
        stateRepository.save(miner);

        block.getTransactions().forEach(this::applyTransaction);
        headBlockId = block.getId();

        if(!stateRepository.getCollectionHash("state").equals(block.getStateHash()))
            throw new RuntimeException("Verified block state hash mismatch.");
    }

    private void applyTransaction(Transaction transaction) {

        Account sender = stateRepository.findAccountByAddress(transaction.getSender());
        Account receiver = stateRepository.findAccountByAddress(transaction.getReceiver());

        if(receiver == null) receiver = new Account(transaction.getReceiver());

        sender.subtract(transaction.getAmount());
        receiver.add(transaction.getAmount());
        stateRepository.save(sender);
        stateRepository.save(receiver);
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
