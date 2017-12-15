package konra.reno.blockchain;

import konra.reno.Crypto;
import konra.reno.KeysDto;
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

    public void init(){


    }

    public KeysDto createAccount(){

        Account acc = Account.create();
        KeyPair keys = Account.popLastKeys();
        KeysDto keysDto = new KeysDto(keys);

        fileService.addAccount(acc);

        p2p.loginCommon();
        p2p.updateFile("accounts");
        p2p.logout();

        p2p.register(acc.getAddress(), keysDto.getPrivateKey(), keysDto.getPrivateKey().substring(0, 4));

        return keysDto;
    }

    public Double login(String publicKey, String privateKey){

        boolean result = p2p.login(publicKey, privateKey, privateKey.substring(0, 4));
        Double balance = null;

        if(result){

            String[] accounts = fileService.readTextFile("accounts").split("\n");

            for(String account: accounts){

                String adr = account.split(":")[0];
                if(!adr.equals(publicKey)) continue;
                balance = Double.valueOf(account.split(":")[1]);
                break;
            }
        }

        return balance;
    }

    public boolean startBlockchain(){

        p2p.loginCommon();
        p2p.addTextFile("balances", "");
        p2p.addTextFile("accounts", "");
        p2p.addTextFile("config", "difficulty=0\n");
        config.put("difficulty", "0");

        Block initialBlock = new Block();
        LinkedList<Block> blockchain = new LinkedList<>();
        blockchain.add(initialBlock);

        fileService.writeBlockchain(blockchain);
        p2p.addFile("blockchain");

        return true;
    }

    public boolean downloadBlockchain(){

        p2p.loginCommon();
        p2p.downloadFile("blockchain");
        p2p.logout();

        return true;
    }

    public boolean verifyBlockchain(StringBuffer sb){

        boolean verified = true;
        LinkedList<Block> blockchain = fileService.readBlockchain();
        String prevPOW = "";

        for(Block b: blockchain){

            if(b.getId() > 2 && !Block.verify(b, prevPOW)){
                verified = false;
                break;
            }

            if(sb != null) sb.append(b.toString() + "\n");
            prevPOW = b.getPOW();
        }

        return verified;
    }

    public boolean addBlock(Block block){

//        p2p.downloadFile("blockchain");
        LinkedList<Block> blockchain = fileService.readBlockchain();

        log.info("Blockchain read  size: " + blockchain.size());
        String prevPOW = blockchain.getLast().getPOW();

        log.info("" + prevPOW + ":" + block.getPreviousPOW());
        if(!Block.verify(block, prevPOW)) return false;

        log.info("passed verification");
        blockchain.add(block);
        fileService.writeBlockchain(blockchain);
//        p2p.updateFile("blockchain");

        log.info("blockchain written");

        return true;
    }
}
