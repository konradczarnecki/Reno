package konra.reno.account;

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

    private FileService fileService;

    @Autowired
    public AccountService(P2PService p2p, Crypto crypto, FileService fileService) {


    }

}
