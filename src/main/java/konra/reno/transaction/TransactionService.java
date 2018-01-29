package konra.reno.transaction;

import konra.reno.core.CoreService;
import konra.reno.transaction.cache.TransactionCacheService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import static java.util.stream.Collectors.*;
import static konra.reno.transaction.TransactionStatus.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionService {

    CoreService core;
    TransactionCacheService cache;
    TransactionRepository repository;

    @Autowired
    public TransactionService(CoreService core, TransactionCacheService cache, TransactionRepository repository) {
        this.core = core;
        this.cache = cache;
        this.repository = repository;
    }

    public Transaction newTx(Transaction transaction, String key) {

        if(!transaction.sign(key)) throw new RuntimeException("Keys mismatch");
        core.addNewTransaction(transaction);
        return transaction;
    }

    // TODO implement CONFIRMED status
    public TransactionStatus checkTxStatus(String hash) {

        TransactionStatus status = UNKNOWN;
        if(core.getTransactionPool().checkIfPending(hash)) status = PENDING;
        if(getTxByHash(hash) != null) status = PROCESSED;
        return status;
    }

    public List<Transaction> getTxsByAddress(String address) {

        return cache.updateAndGet(address);
    }

    public Transaction getTxByHash(String hash) {

        return repository.getTxByHash(hash);
    }
}
