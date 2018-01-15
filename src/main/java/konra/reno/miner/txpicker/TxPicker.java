package konra.reno.miner.txpicker;

import konra.reno.transaction.Transaction;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TxPicker {

    TxPickerConfig config;

    @Autowired
    public TxPicker(TxPickerConfig config) {
        this.config = config;
    }

    public Set<Transaction> pick(Set<Transaction> pool) {

        Set<Transaction> chosen = new HashSet<>();

        switch(config.getStrategy()) {

            case HIGHIEST_FEE:
                chosen = pool.stream()
                        .sorted(Comparator.comparingDouble(Transaction::getFee))
                        .limit(config.getMaxTxPerBlock())
                        .collect(Collectors.toSet());
                break;

            case OLDEST:
                chosen = pool.stream()
                        .sorted(Comparator.comparingLong(Transaction::getTimestamp))
                        .limit(config.getMaxTxPerBlock())
                        .collect(Collectors.toSet());
                break;
        }

        return chosen;
    }
}
