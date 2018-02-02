package konra.reno.core;

import konra.reno.account.Account;
import konra.reno.core.block.Block;
import konra.reno.core.callback.CallbackHandler;
import konra.reno.core.callback.CallbackType;
import konra.reno.core.persistance.BlockRepository;
import konra.reno.core.persistance.RenoRepository;
import konra.reno.core.persistance.StateRepository;
import konra.reno.core.block.BlockConfiguration;
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

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class CoreService {

    BlockRepository blockRepository;
    StateRepository stateRepository;
    CoreConfig config;
    @Getter BlockConfiguration blockConfiguration;
    @Getter CallbackHandler callbackHandler;

    @Getter TransactionPool transactionPool;
    @Getter Block headBlock;
    @Getter @Setter long networkHead;

    @Autowired
    public CoreService(BlockRepository blockRepository,
                       StateRepository stateRepository,
                       BlockConfiguration blockConfiguration,
                       CoreConfig config,
                       CallbackHandler callbackHandler) {

        this.blockRepository = blockRepository;
        this.stateRepository = stateRepository;
        this.blockConfiguration = blockConfiguration;
        this.callbackHandler = callbackHandler;
        this.config = config;
        this.transactionPool = new TransactionPool();
    }

    @Transactional
    public boolean startBlockchain() {

        Block initialBlock = new Block(null);
        blockRepository.save(initialBlock);
        setHeadBlock(initialBlock);

        return true;
    }

    public void setHeadBlock(Block head) {

        headBlock = head;
        callbackHandler.execute(CallbackType.HEAD_EXCHANGE);
        callbackHandler.execute(CallbackType.MINE_NEW_BLOCK);

        log.info("New head: " + headBlock.toString());
    }

    public List<Block> getBlocks(long fromId, long count) {

        return blockRepository.findBlocksByIdBetween(fromId - 1, fromId + count);
    }

    public List<Block> getBlocks(long fromId) {

        return blockRepository.findBlocksByIdIsGreaterThanEqual(fromId);
    }

    synchronized public void processNewBlocks(List<Block> incomingBlocks) {

        incomingBlocks.forEach(this::processBlock);
    }

    public void rollbackFromBlock(long blockId) {

        while(headBlock.getId() >= blockId) rollbackLastBlock();
    }

    synchronized public void addNewTransaction(Transaction transaction) {

        transactionPool.addPending(transaction);
        callbackHandler.execute(CallbackType.TRANSACTION, transaction);
    }

    private void processBlock(Block block) {

        log.debug("Processing block " + block.toString());

        if(verifyBlock(block)) acceptBlock(block);
        else throw new RuntimeException("Invalid block exception");
    }

    private boolean verifyBlock(Block block) {

        Block currentHead = blockRepository.findTopByOrderByIdDesc();

        return block.getId() - 1 == currentHead.getId() &&
                block.getPreviousPOW().equals(currentHead.getPow()) &&
                block.validate(blockConfiguration.getDifficulty(block)) &&
                verifyTransactions(block);
    }

    private boolean verifyTransactions(Block block) {

        Set<Transaction> transactions = block.getTransactions();
        if(!transactions.stream().allMatch(Transaction::validate)) return false;

        Map<String, Double> summedMap = getSummedSendersTxMap(transactions);

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

        miner.add(blockConfiguration.getReward(block));
        miner.add(getSummedFees(block.getTransactions()));
        stateRepository.save(miner);

        block.getTransactions().forEach(this::applyTransaction);
        transactionPool.removeFromPool(block.getTransactions());

        setHeadBlock(block);
    }

    private void applyTransaction(Transaction transaction) {

        Account sender = stateRepository.findAccountByAddress(transaction.getSender());
        Account receiver = stateRepository.findAccountByAddress(transaction.getReceiver());

        if(receiver == null) receiver = new Account(transaction.getReceiver());

        sender.subtract(transaction.getAmount());
        sender.subtract(transaction.getFee());
        receiver.add(transaction.getAmount());

        stateRepository.save(sender);
        stateRepository.save(receiver);
    }

    @Transactional
    protected void rollbackTransaction(Transaction transaction) {

        Account sender = stateRepository.findAccountByAddress(transaction.getSender());
        Account receiver = stateRepository.findAccountByAddress(transaction.getReceiver());

        sender.add(transaction.getAmount());
        sender.add(transaction.getFee());
        receiver.subtract(transaction.getAmount());

        stateRepository.save(sender);
        stateRepository.save(receiver);
    }

    private void rollbackLastBlock() {

        blockRepository.removeBlocksByIdIsGreaterThan(headBlock.getId() - 1);

        Account miner = stateRepository.findAccountByAddress(headBlock.getMiner());
        miner.subtract(blockConfiguration.getReward(headBlock));
        miner.subtract(getSummedFees(headBlock.getTransactions()));
        stateRepository.save(miner);

        headBlock.getTransactions().forEach(this::rollbackTransaction);
        headBlock = blockRepository.findBlockById(headBlock.getId() - 1);
    }

    public static Map<String, Double> getSummedSendersTxMap(Set<Transaction> transactions) {

        return transactions.stream()
                .collect(groupingBy(Transaction::getSender, summingDouble(Transaction::getAmount)));
    }

    public static long getSummedFees(Set<Transaction> transactions) {

        return transactions.stream()
                .map(Transaction::getFee)
                .reduce(0L, (partial, fee) -> partial + fee);
    }
}
