package konra.reno.miner;

import konra.reno.Crypto;
import konra.reno.blockchain.Block;
import konra.reno.blockchain.CoreService;
import konra.reno.blockchain.FileService;
import konra.reno.blockchain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

@Service
public class MinerService {

    private static final Logger log = LoggerFactory.getLogger(MinerService.class);

    private CoreService core;
    private FileService fileService;

    List<Transaction> transactions;

    ScheduledExecutorService exec;
    boolean stopMining;

    @Autowired
    public MinerService(CoreService core, FileService fileService) {

        this.core = core;
        this.fileService = fileService;
        this.exec = Executors.newScheduledThreadPool(10);
        this.stopMining = false;
    }

    public synchronized void startMining(){

        if(stopMining) return;

        exec = exec == null ? Executors.newScheduledThreadPool(10) : exec;

        LinkedList<Block> blockchain = fileService.readBlockchain();
        Block lastBlock = blockchain.getLast();
        Block newBlock = new Block(lastBlock);

        Runnable mine = () -> {

             Block verified = mineBlock(newBlock);
             if(verified != null){

                 if(core.addBlock(verified))
                    log.info("Block mined." + verified.toString());
             }

             startMining();
        };

        exec.execute(mine);
    }

    public void stopMining(){

        exec.shutdownNow();
        exec = null;
        this.stopMining = true;
    }

    public Block mineBlock(Block raw){

        log.info("Mining block {}...", raw.getId());

        for(long n = 0; n < Long.MAX_VALUE-1; n++){

            raw.setNonce(n);
            String hash = raw.hash();

            if(Block.verifyPOW(raw, hash)) {

                raw.setPOW(hash);
                return raw;
            }
        }

        return null;
    }

    public void ready(){

        this.stopMining = false;
    }



}
