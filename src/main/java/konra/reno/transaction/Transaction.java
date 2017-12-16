package konra.reno.transaction;

import java.util.Date;

public class Transaction {

    String sender;
    String receiver;
    double amount;
    long timestamp;
    String signature;

    public Transaction(String sender, String receiver, double amount) {

        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.timestamp = new Date().getTime();
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public double getAmount() {
        return amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
