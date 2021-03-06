package konra.reno.transaction;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class TransactionPool {

    @Setter boolean fillPool;
    Map<String, Transaction> pool;
    Map<String, Transaction> pending;

    public TransactionPool() {

        fillPool = true;
        pool = new HashMap<>();
        pending = new HashMap<>();
    }

    public void addPending(Transaction t) {
        pending.put(t.getHash(), t);
        if(fillPool) pool.put(t.getHash(), t);
    }

    public void removePending(Transaction t) {
        pending.remove(t.getHash());
    }

    public boolean pendingTransactions() {
        return !pending.isEmpty();
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

    public void removeFromPool(Set<Transaction> transactions) {

        for(Transaction tx: transactions) {

            pool.put(tx.getHash(), null);
            if(pendingTransactions()) pending.put(tx.getHash(), null);
        }
    }

    public Set<Transaction> txsInPool() {

        return pool.values()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
