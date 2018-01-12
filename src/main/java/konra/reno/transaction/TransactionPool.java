package konra.reno.transaction;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class TransactionPool {

    @Setter boolean fillPool;
    Set<Transaction> pool;
    Set<Transaction> pending;

    public TransactionPool() {

        fillPool = false;
        pool = new HashSet<>();
        pending = new HashSet<>();
    }

    public void addPending(Transaction t) {
        pending.add(t);
    }

    public boolean checkIfPending(Transaction t) {
        return pending.contains(t);
    }

    public void addToPool(Transaction t) {
        if(fillPool) pool.add(t);
    }
}
