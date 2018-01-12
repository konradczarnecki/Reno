package konra.reno.transaction;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class TransactionPool {

    boolean fillPool;
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
}
