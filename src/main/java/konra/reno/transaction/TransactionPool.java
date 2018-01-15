package konra.reno.transaction;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class TransactionPool {

    @Setter boolean fillPool;
    Map<String, Transaction> pool;
    Map<String, Transaction> pending;

    public TransactionPool() {

        fillPool = false;
        pool = new HashMap<>();
        pending = new HashMap<>();
    }

    public void addPending(Transaction t) {
        pending.put(t.getHash(), t);
    }

    public boolean checkIfPending(String hash) {
        return pending.containsKey(hash);
    }

    public boolean checkIfPending(Transaction t) {
        return pending.containsKey(t.getHash());
    }

    public void addToPool(Transaction t) {
        if(fillPool) pool.put(t.getHash(), t);
    }
}
