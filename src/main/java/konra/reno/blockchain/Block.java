package konra.reno.blockchain;

import konra.reno.Crypto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Block implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(Block.class);

    private int id;
    private long nonce;
    private List<Transaction> transactions;
    private String previousPOW;
    private int difficulty;
    private String pow;

    public Block() {

        id = 1;
        difficulty = 0;
        transactions = new ArrayList<>();
        previousPOW = "";
    }

    public Block(Block previousBlock){

        id = previousBlock.id + 1;
        difficulty = 3;
        transactions = new ArrayList<>();
        previousPOW = previousBlock.pow;
    }

    public String hash(){

        StringBuffer source = new StringBuffer();
        source.append(id);
        source.append(nonce);
        source.append(Math.abs(transactions.hashCode()));
        source.append(previousPOW);
        return Crypto.hashB64(source.toString());
    }

    public static boolean verify(Block block, String prevPOW){

        String hash = block.hash();
        if(!hash.equals(block.getPOW())) return false;
        log.debug("pow matches");

        if(block.getId() > 2 && !block.getPreviousPOW().equals(prevPOW)) return false;
        return verifyPOW(block, hash);
    }

    public static boolean verifyPOW(Block block, String pow){

        if(block.getDifficulty() == 0) return true;
        else {

            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < block.getDifficulty(); i++) sb.append("0");

            return pow.substring(0, block.getDifficulty()).equals(sb.toString());
        }
    }

    public int getId() {
        return id;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public String getPreviousPOW() {
        return previousPOW;
    }

    public String getPOW() {
        return pow;
    }

    public void setPOW(String POW) {
        this.pow = POW;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public String toString(){

        return "\n----------------\nBlock " + id + "\n" +
                "Transactions: " + transactions.size() + "\n" +
                "POW:          " + pow + "\n" +
                "Previous POW: " + previousPOW + "\n" +
                "---------------\n";
    }
}
