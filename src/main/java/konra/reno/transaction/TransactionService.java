package konra.reno.transaction;

import konra.reno.core.CoreService;
import konra.reno.transaction.cache.TransactionCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    CoreService core;
    TransactionCache cache;

    @Autowired
    public TransactionService(CoreService core, TransactionCache cache) {
        this.core = core;
        this.cache = cache;
    }

    public Transaction newTx(Transaction transaction, String key) {

        if(!transaction.sign(key)) throw new RuntimeException("Keys mismatch");
        core.addNewTransaction(transaction);
        return transaction;
    }

    public List<Transaction> getTxsByAddress(String address) {

        TransactionCache
    }

    public Transaction getTxByHash(String hash) {

    }

    public static Map<String, Double> getSummedTxMap(List<Transaction> transactions) {

        Map<String, Double> summedMap = new HashMap<>();

        transactions.forEach(transaction -> {

            String sender = transaction.getSender();

            if(summedMap.containsKey(sender)) summedMap.put(sender, summedMap.get(sender) + transaction.getAmount());
            else summedMap.put(sender, 0d);
        });

        return summedMap;
    }

    public static double collectFees(List<Transaction> transactions) {

        return transactions.stream()
                .map(Transaction::getFee)
                .reduce(0d, (partial, fee) -> partial + fee);
    }
}
