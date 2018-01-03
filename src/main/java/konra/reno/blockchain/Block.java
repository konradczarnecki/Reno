package konra.reno.blockchain;

import konra.reno.util.Crypto;
import konra.reno.transaction.Transaction;
import lombok.Getter;
import lombok.Setter;
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

    public String data() {

        StringBuilder sb = new StringBuilder();
        sb.append("@").append(id).append("\n");
        sb.append(transactions.size()).append(":").append(nonce).append(":").append(difficulty).append("\n");
        sb.append(pow).append(":").append(previousPOW).append("\n");
        for(Transaction t: transactions) sb.append(t.data()).append("/");

        return sb.toString();
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

    public void bumpNonce() {

        nonce++;
        this.hashCache = "";
    }


    public String toString(){

        return "\n----------------\nBlock " + id + "\n" +
                "Transactions: " + transactions.size() + "\n" +
                "POW:          " + pow + "\n" +
                "Previous POW: " + previousPOW + "\n" +
                "---------------\n";
    }

    public static Block parse(String data) {

        Block b = new Block();
        String[] lines = data.split("\n");

        long id = Long.valueOf(lines[0].substring(1));

        String[] secondLine = lines[1].split(":");
        int transactionCount = Integer.parseInt(secondLine[0]);
        long nonce = Long.parseLong(secondLine[1]);
        int difficulty = Integer.parseInt(secondLine[2]);

        String[] thirdLine = lines[2].split(":");
        String pow = thirdLine[0];
        String prevPow = thirdLine[1];

        String[] transactions = lines[3].split("/");

        for(String transaction: transactions) {

            Transaction t = Transaction.parse(transaction);
            b.transactions.add(t);
        }

        b.id = id;
        b.nonce = nonce;
        b.difficulty = difficulty;
        b.pow = pow;
        b.previousPOW = prevPow;

        if(b.transactions.size() != transactionCount || !b.hash().equals(pow))
            throw new RuntimeException("Invalid block, id: " + b.id);

        return b;
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
