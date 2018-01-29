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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
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

    public Account login(String publicKey, String privateKey) {

        // TODO after implemented change to KeysMismatchException
        if(!Crypto.testKeys(publicKey, privateKey)) throw new RuntimeException("Keys don't match.");

        Account account = stateRepository.findAccountByAddress(publicKey);
        if(account == null) account = new Account(publicKey);
        return account;
    }
}
