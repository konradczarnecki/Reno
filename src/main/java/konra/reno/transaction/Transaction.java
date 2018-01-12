package konra.reno.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import konra.reno.crypto.Crypto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.commons.codec.digest.Crypt;

import java.util.Date;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class Transaction {

    long timestamp;
    String sender;
    String receiver;
    double amount;
    double fee;
    String message;
    String signature;

    public Transaction(String sender, String receiver, double amount, double fee) {

        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.fee = fee;
        this.timestamp = new Date().getTime();
        this.message = "";
    }

    public Transaction(String sender, String receiver, Double amount, double fee, String message){

        this(sender, receiver, amount, fee);
        this.message = message;
    }

    public boolean sign(String privateKey) {

        if(!Crypto.testKeys(sender, privateKey)) return false;
        signature = Crypto.sign(privateKey, hash());
        return true;
    }

    @SneakyThrows
    public String data() {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    public String hash() {

        StringBuilder sb = new StringBuilder().
            append(timestamp).
            append(sender).
            append(receiver).
            append(amount).
            append(fee).
            append(message);

        return Crypto.hash(sb.toString());
    }

    public boolean equals(Object o) {

        if(o == null || o.getClass() != getClass()) return false;
        Transaction other = (Transaction) o;
        return this.hash().equals(other.hash());
    }

    @SneakyThrows
    public static Transaction parse(String data) {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(data, Transaction.class);
    }

    public static boolean validate(Transaction t) {

        return Crypto.verifySignature(t.signature, t.sender, t.hash());
    }
}
