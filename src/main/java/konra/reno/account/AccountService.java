package konra.reno.account;

import konra.reno.core.CoreConfig;
import konra.reno.core.CoreService;
import konra.reno.core.persistance.StateRepository;
import konra.reno.p2p.P2PService;
import konra.reno.crypto.Crypto;
import konra.reno.transaction.Transaction;
import konra.reno.util.FileService;
import konra.reno.util.KeysDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class AccountService {

    CoreService core;
    CoreConfig coreConfig;
    StateRepository stateRepository;

    @Autowired
    public AccountService(CoreService core, CoreConfig coreConfig, StateRepository stateRepository) {
        this.core = core;
        this.coreConfig = coreConfig;
        this.stateRepository = stateRepository;
    }

    public Account createAccount() {

        return Account.create();
    }

    public Account login(KeysDto keys) {

        // TODO after implemented change to KeysMismatchException
        if(!Crypto.testKeys(keys.getPublicKey(), keys.getPrivateKey())) throw new RuntimeException("Keys don't match.");

        Account account = stateRepository.findAccountByAddress(keys.getPublicKey());
        if(account == null) account = new Account(keys.getPublicKey());
        return account;
    }

    public Account login(String encryptedKeyfile, String password) {

        KeysDto keys = decryptKeyfile(encryptedKeyfile, password);
        return login(keys);
    }

    public Account getAccountByAddress(String address) {

        Account account = stateRepository.findAccountByAddress(address);
        account = account != null ? account : new Account(address);

        return account;
    }

    public KeysDto decryptKeyfile(String encryptedKeyfile, String password) {

        String[] decrypted = Crypto.decryptHexSymmetric(passwordPadding(password), encryptedKeyfile).split(":");
        return new KeysDto(decrypted[0], decrypted[1]);
    }

    public String encryptKeyfile(KeysDto keys, String password) {

        String keysDecrypted = keys.getPublicKey() + ":" + keys.getPrivateKey();
        return Crypto.encryptHexSymmetric(passwordPadding(password), keysDecrypted);
    }

    public String encryptKeyfile(String publicKey, String privateKay, String password) {

        KeysDto keys = new KeysDto(publicKey, privateKay);
        return encryptKeyfile(keys, password);
    }

    private String passwordPadding(String unpadded) {

        if(unpadded.length() == 16) return unpadded;

        String padded = unpadded;

        if(unpadded.length() < 16)
            for(int i = unpadded.length(); i < 16; i++) padded += 'a';

        else padded = unpadded.substring(0, 16);

        return padded;
    }
}
