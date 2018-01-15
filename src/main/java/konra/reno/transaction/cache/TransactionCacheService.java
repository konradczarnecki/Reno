package konra.reno.transaction.cache;

import konra.reno.core.Block;
import konra.reno.core.CoreService;
import konra.reno.transaction.Transaction;
import konra.reno.transaction.TransactionRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionCacheService {

    TransactionCacheRepository cacheRepository;
    TransactionRepository transactionRepository;
    CoreService core;

    @Autowired
    public TransactionCacheService(TransactionCacheRepository cacheRepository, CoreService core, TransactionRepository transactionRepository) {
        this.cacheRepository = cacheRepository;
        this.core = core;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public List<Transaction> updateAndGet(String address) {

        TransactionCache cache = updateCacheFor(address);
        return cache.getTransactions();
    }

    @Transactional
    protected TransactionCache updateCacheFor(String address) {

        TransactionCache cache = cacheRepository.findByAddressIs(address);
        long updateFrom = cache != null ? cache.getLastCheckedBlock() : 1;
        cache = cache != null ? cache : new TransactionCache();

        List<Transaction> transactions = transactionRepository.getTxsByAddress(address, updateFrom);
        cache.setLastCheckedBlock(core.getHeadBlock().getId());
        cache.getTransactions().addAll(transactions);
        cacheRepository.save(cache);
        return cache;
    }
}
