package konra.reno.account;

import konra.reno.p2p.P2PService;
import konra.reno.util.Crypto;
import konra.reno.util.FileService;
import konra.reno.util.KeysDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.concurrent.Executors;

@Service
public class AccountService {

    private P2PService p2p;
    private FileService fileService;
    private Crypto crypto;

    @Autowired
    public AccountService(P2PService p2p, Crypto crypto, FileService fileService) {

        this.p2p = p2p;
        this.crypto = crypto;
        this.fileService = fileService;
    }

    public KeysDto createAccount(){

        Account acc = Account.create();
        KeyPair keys = Account.popLastKeys();
        KeysDto keysDto = new KeysDto(keys);

        fileService.appendTxt("accounts", acc.toString());

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
}
