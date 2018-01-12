package konra.reno.account;

import konra.reno.core.CoreConfig;
import konra.reno.core.CoreService;
import konra.reno.p2p.P2PService;
import konra.reno.crypto.Crypto;
import konra.reno.transaction.Transaction;
import konra.reno.util.FileService;
import konra.reno.util.KeysDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;

@Service
public class AccountService {

    CoreService core;
    CoreConfig coreConfig;

    @Autowired
    public AccountService(CoreService core, CoreConfig coreConfig) {
        this.core = core;
        this.coreConfig = coreConfig;
    }

    public KeysDto createAccount() {

        Account account = Account.create();
        Transaction t = new Transaction(coreConfig.getSourceAccount(), account.getAddress(), 0, 0);
    }
}
