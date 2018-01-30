package konra.reno.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import konra.reno.crypto.Crypto;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.codec.digest.Crypt;

import java.util.Date;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Getter
public class Transaction {

    long timestamp;
    String sender;
    String receiver;
    long amount;
    long fee;
    String message;
    String hash;
    String signature;

    @JsonCreator
    public Transaction(@JsonProperty("sender") String sender,
                       @JsonProperty("receiver") String receiver,
                       @JsonProperty("amount") long amount,
                       @JsonProperty("fee") long fee,
                       @JsonProperty("message") String message) {

        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.fee = fee;
        this.message = message;
        this.timestamp = new Date().getTime();
    }

    public boolean sign(String privateKey) {

        if(!Crypto.testKeys(sender, privateKey)) return false;
        hash = hash();
        signature = Crypto.sign(privateKey, hash);
        return true;
    }

    public boolean validate() {

        return Crypto.verifySignature(signature, sender, hash());
    }

    @SneakyThrows
    public String data() {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    private String hash() {

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
}
