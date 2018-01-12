package konra.reno.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import konra.reno.crypto.Crypto;
import konra.reno.transaction.Transaction;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "blockchain")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter @Setter
@Slf4j
public class Block {

    @Id
    long id;
    long nonce;
    List<Transaction> transactions;
    String previousPOW;
    int difficulty;
    String pow;
    String miner;
    String stateHash;

    @Transient String hashCache;
    @Transient String transactionsHashCache;

    private Block() {

        id = 1;
        transactions = new ArrayList<>();
        previousPOW = "";
        hashCache = "";
        transactionsHashCache = "";
    }

    public Block(Block previousBlock) {

        this();
        difficulty = 3;

        if(previousBlock != null){
            id = previousBlock.id + 1;
            previousPOW = previousBlock.pow;
        }
    }

    public void bumpNonce() {

        nonce++;
        this.hashCache = "";
    }

    public String hash() {

        if(!hashCache.equals("")) return hashCache;

        if(transactionsHashCache.equals("")) {

            StringBuilder transactionsHash = new StringBuilder();
            for(Transaction t : transactions) transactionsHash.append(t.getHash());
            transactionsHashCache = transactionsHash.toString();
        }

        hashCache = Crypto.hash(String.valueOf(id) + nonce + transactionsHashCache + previousPOW);

        return hashCache;
    }

    @SneakyThrows
    public String data() {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    public String toString(){

        return "\n----------------\nBlock " + id + "\n" +
                "Transactions: " + transactions.size() + "\n" +
                "POW:          " + pow + "\n" +
                "Previous POW: " + previousPOW + "\n" +
                "---------------\n";
    }

    @SneakyThrows
    public static Block parse(String data) {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(data, Block.class);
    }

    public static boolean validate(Block block, String prevPOW) {

        String hash = block.hash();
        if (!hash.equals(block.getPow())) return false;
        log.debug("pow matches");

        return (block.getId() == 1 || block.getPreviousPOW().equals(prevPOW)) && verifyPOW(block, hash);
    }

    public static boolean verifyPOW(Block block, String pow){

        if(block.getDifficulty() == 0) return true;
        else {

            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < block.getDifficulty(); i++) sb.append("0");

            return pow.substring(0, block.getDifficulty()).equals(sb.toString());
        }
    }
}
