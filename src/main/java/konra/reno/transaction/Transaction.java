package konra.reno.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import konra.reno.util.Crypto;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Date;

public class Transaction {

    @Getter private long timestamp;
    @Getter private String sender;
    @Getter private String receiver;
    @Getter private double amount;
    @Getter private double fee;
    @Getter private String message;
    @Getter private String signature;

    private Transaction(String sender, String receiver, double amount, double fee) {

        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.fee = fee;
        this.timestamp = new Date().getTime();
    }

    public Transaction(String sender, String receiver, Double amount, double fee, String message){

        this(sender, receiver, amount, fee);
        this.message = message;
    }

    private Transaction() {}

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

        return Crypto.hashHex(sb.toString());
    }

    @SneakyThrows
    public static Transaction parse(String data) {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(data, Transaction.class);
    }
}
