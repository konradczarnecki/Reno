package konra.reno.blockchain;

import konra.reno.transaction.Transaction;
import konra.reno.util.Crypto;
import konra.reno.util.FileService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class CoreService {

    FileService fileService;
    Crypto crypto;
    BlockRepository blockRepository;

    Map<String, String> config;
    ScheduledExecutorService exec;
    Runnable syncCallback;

    @Getter long headBlockId;
    @Getter @Setter long networkHead;

    @Autowired
    public CoreService(Crypto crypto, FileService fileService, BlockRepository blockRepository) {

        this.crypto = crypto;
        this.fileService = fileService;
        this.blockRepository = blockRepository;
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
                !Block.verify(block, currentHead.getPreviousPOW())||
                !verifyTransactions(block.getTransactions())) return false;

    }

    private boolean verifyTransactions(List<Transaction> transactions) {

        Map<String, Double> stateDelta = new HashMap<>();

        transactions.forEach(transaction -> {
            String sender = transaction.getSender();
            String receiver = transaction.getReceiver();

            if(!stateDelta.containsKey(sender)) stateDelta.put(sender, 0d);
            stateDelta.put(sender, stateDelta.get(sender) - transaction.getAmount());

            if(!stateDelta.containsKey(receiver)) stateDelta.put(receiver, 0d);
            stateDelta.put(receiver, stateDelta.get(receiver) + transaction.getAmount());
        });


     }

    private void acceptBlock() {

    }
}
