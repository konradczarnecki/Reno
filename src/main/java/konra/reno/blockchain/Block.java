package konra.reno.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import konra.reno.util.Crypto;
import konra.reno.transaction.Transaction;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "block")
public class Block {

    private static final Logger log = LoggerFactory.getLogger(Block.class);

    @Id
    @Field("id")
    @Getter private long id;

    @Field("nonce")
    @Getter private long nonce;

    @Field("transactions")
    @Getter private List<Transaction> transactions;

    @Field("prevPOW")
    @Getter private String previousPOW;

    @Field("difficulty")
    @Getter private int difficulty;

    @Field("pow")
    @Getter @Setter private String pow;

    @Field("miner")
    @Getter @Setter private String miner;

    @Field("stateHash")
    @Getter @Setter private String stateHash;

    private String hashCache;
    private String transactionsHashCache;

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
            for(Transaction t : transactions) transactionsHash.append(t.hash());
            transactionsHashCache = transactionsHash.toString();
        }

        hashCache = Crypto.hashHex(String.valueOf(id) + nonce + transactionsHashCache + previousPOW);

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

    public static boolean verify(Block block, String prevPOW) {

        String hash = block.hash();
        if (!hash.equals(block.getPow())) return false;
        log.debug("pow matches");

        return (block.getId() <= 1 || block.getPreviousPOW().equals(prevPOW)) && verifyPOW(block, hash);
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
