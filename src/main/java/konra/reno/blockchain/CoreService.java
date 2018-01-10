package konra.reno.blockchain;

import konra.reno.account.Account;
import konra.reno.transaction.Transaction;
import konra.reno.util.Crypto;
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

    FileService fileService;
    Crypto crypto;
    BlockRepository blockRepository;
    StateRepository stateRepository;

    Map<String, String> config;
    ScheduledExecutorService exec;
    Runnable syncCallback;

    @Getter long headBlockId;
    @Getter @Setter long networkHead;

    @Autowired
    public CoreService(Crypto crypto, FileService fileService, BlockRepository blockRepository, StateRepository stateRepository) {

        this.crypto = crypto;
        this.fileService = fileService;
        this.blockRepository = blockRepository;
        this.stateRepository = stateRepository;
        this.config = new HashMap<>();
        exec  = Executors.newScheduledThreadPool(10);
    }

    @Transactional
    public boolean startBlockchain() {

        Block initialBlock = new Block(null);
        blockRepository.save(initialBlock);
        setHeadBlockId(initialBlock.getId());

        return true;
    }

    public void registerSyncCallback(Runnable callback) {

        syncCallback = callback;
    }

    public void setHeadBlockId(long id) {

        headBlockId = id;
        syncCallback.run();
    }

    public List<Block> getBlocks(long fromId, long toId) {

        return blockRepository.findBlocksByIdBetween(fromId, toId);
    }

    public boolean verifyIncomingBlocks(List<Block> incomingBlocks) {

    }

    private boolean processBlock(Block block) {



    }

    private boolean verifyBlock(Block block) {

        Block currentHead = blockRepository.findTopByOrderByIdDesc();
        if(
                block.getId() - 1 != currentHead.getId() ||
                !Block.validate(block, currentHead.getPreviousPOW())||
                !verifyTransactions(block.getTransactions())) return false;

    }

    private boolean verifyTransactions(List<Transaction> transactions) {

        if(!transactions.stream().allMatch(Transaction::validate)) return false;

        Map<String, Double> summedMap = getSummedTransactionsMap(transactions);

        for(Map.Entry<String, Double> entry: summedMap.entrySet()) {

            Account acc = stateRepository.findAccountByAddress(entry.getKey());
            if(acc == null || acc.getBalance() < entry.getValue()) return false;
        }

        if(transactions.stream().filter(transaction -> transaction.getSender().equals("0") && transaction.getAmount() != 0).count() != 1) return false;

        return true;
    }

    private Map<String, Double> getSummedTransactionsMap(List<Transaction> transactions) {

        Map<String, Double> summedMap = new HashMap<>();

        transactions.stream().filter(transaction -> !transaction.getSender().equals("0")).forEach(transaction -> {

            String sender = transaction.getSender();

            if(summedMap.containsKey(sender)) summedMap.put(sender, summedMap.get(sender) + transaction.getAmount());
            else summedMap.put(sender, 0d);
        });

        return summedMap;
    }


    private void acceptBlock() {

    }
}
