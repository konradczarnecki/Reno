package konra.reno.core.block;

import com.fasterxml.jackson.databind.ObjectMapper;
import konra.reno.crypto.Crypto;
import konra.reno.transaction.Transaction;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "blockchain")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
@Slf4j
public class Block {

    @Id
    long id;
    long nonce;
    long timestamp;
    Set<Transaction> transactions;
    String previousPOW;
    String pow;
    String miner;
    String message;

    @Transient
    String transactionsHashCache;

    public Block(Block previousBlock) {

        nonce = 0;
        transactions = new HashSet<>();
        timestamp = System.currentTimeMillis();
        pow = "";
        transactionsHashCache = "";

        if (previousBlock != null) {
            id = previousBlock.id + 1;
            previousPOW = previousBlock.pow;
        } else {
            id = 1;
            previousPOW = "reno" + timestamp;
        }
    }

    public void bumpNonce() {

        if(nonce == Long.MAX_VALUE) nonce = 0;
        nonce++;
    }

    public String hash() {

        if (transactionsHashCache.equals("")) {

            StringBuilder transactionsHash = new StringBuilder();
            transactions.forEach(transaction -> transactionsHash.append(transaction.getHash()));
            transactionsHashCache = Crypto.hash(transactionsHash.toString());
        }

        return Crypto.hash(String.valueOf(id) + nonce + transactionsHashCache + previousPOW);
    }

    public void setTransactions(Set<Transaction> transactions) {
        transactionsHashCache = "";
        this.transactions = transactions;
    }

    @SneakyThrows
    public String data() {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

//    public String toString() {
//
//        return "\n----------------\nBlock " + id + "\n" +
//                "Transactions: " + transactions.size() + "\n" +
//                "POW:          " + pow + "\n" +
//                "Previous POW: " + previousPOW + "\n" +
//                "---------------\n";
//    }

    @SneakyThrows
    public static Block parse(String data) {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(data, Block.class);
    }

    public boolean validate(int difficulty) {

        return hash().equals(pow) && verifyPOW(difficulty);
    }

    public boolean verifyPOW(int difficulty) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < difficulty; i++) sb.append("0");
        return difficulty == 0 || pow.substring(0, difficulty).equals(sb.toString());
    }
}
