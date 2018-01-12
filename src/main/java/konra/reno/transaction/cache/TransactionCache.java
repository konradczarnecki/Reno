package konra.reno.transaction.cache;

import konra.reno.transaction.Transaction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "transaction_cache")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter @Setter
public class TransactionCache {

    @Id
    String address;
    long lastCheckedBlock;
    List<Transaction> transactions;
}
