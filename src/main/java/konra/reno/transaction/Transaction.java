package konra.reno.transaction;

import konra.reno.util.Crypto;
import lombok.Getter;

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

    public String data() {

        StringBuilder sb = new StringBuilder().
            append(timestamp).append(":").
            append(sender).append(":").
            append(receiver).append(":").
            append(amount).append(":").
            append(fee).append(":").
            append(message).append(":").
            append(hash()).append(":").
            append(signature);

        return sb.toString();
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

    public static Transaction parse(String data) {

        String[] props = data.split(":");
        Transaction t = new Transaction();
        t.timestamp = Long.parseLong(props[0]);
        t.sender = props[1];
        t.receiver = props[2];
        t.amount = Double.parseDouble(props[3]);
        t.fee = Double.parseDouble(props[4]);
        t.message = props[5];
        t.signature = props[7];

        if(t.hash().equals(props[6])) return t;
        else return null;
    }
}
