package konra.reno.miner;

import konra.reno.blockchain.Block;
import konra.reno.blockchain.CoreService;
import konra.reno.util.FileService;
import konra.reno.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

    }

    public void stopMining(){

        exec.shutdownNow();
        exec = null;
        this.stopMining = true;
    }

    public Block mineBlock(Block raw){

        log.info("Mining block {}...", raw.getId());

        for(long n = 0; n < Long.MAX_VALUE-1; n++){

            raw.bumpNonce();
            String hash = raw.hash();

            if(Block.verifyPOW(raw, hash)) {

                raw.setPow(hash);
                return raw;
            }
        }

        return null;
    }

    public void ready(){

        this.stopMining = false;
    }



}
