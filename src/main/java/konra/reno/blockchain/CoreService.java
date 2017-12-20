package konra.reno.blockchain;

import konra.reno.util.Crypto;
import konra.reno.util.FileService;
import konra.reno.util.KeysDto;
import konra.reno.account.Account;
import konra.reno.p2p.P2PService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class CoreService {

    private static final Logger log = LoggerFactory.getLogger(CoreService.class);

    private P2PService p2p;
    private FileService fileService;
    private Crypto crypto;

    private Map<String, String> config;
    private ScheduledExecutorService exec;


    @Autowired
    public CoreService(P2PService p2p, Crypto crypto, FileService fileService) {

        this.p2p = p2p;
        this.crypto = crypto;
        this.fileService = fileService;
        this.config = new HashMap<>();
        exec  = Executors.newScheduledThreadPool(10);
    }

    public boolean startBlockchain(){

        config.put("difficulty", "0");

        Block initialBlock = new Block();
        LinkedList<Block> blockchain = new LinkedList<>();
        blockchain.add(initialBlock);

        fileService.writeBlockchain(blockchain);

        return true;
    }

    public boolean downloadBlockchain(){


        return true;
    }

    public boolean verifyBlockchain(StringBuilder sb){

        boolean verified = true;
        LinkedList<Block> blockchain = fileService.readBlockchain();
        String prevPOW = "";

        for(Block b: blockchain){

            if(b.getId() > 2 && !Block.verify(b, prevPOW)){
                verified = false;
                break;
            }

            log.info(b.toString());
            if(sb != null){
                sb.append(b.toString());
                sb.append("\n");
            }

            prevPOW = b.getPOW();
        }

        return verified;
    }

    public boolean addBlock(Block block){


        LinkedList<Block> blockchain = fileService.readBlockchain();

        String prevPOW = blockchain.getLast().getPOW();

        if(!Block.verify(block, prevPOW)) return false;

        log.debug("passed verification");
        blockchain.add(block);
        fileService.writeBlockchain(blockchain);


        log.debug("Block added.");

        return true;
    }
}
