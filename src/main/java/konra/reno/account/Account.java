package konra.reno.account;

import konra.reno.util.Crypto;

import java.io.UnsupportedEncodingException;
import java.security.*;

public class Account {

    private static final Crypto crypto = new Crypto();
    private static KeyPair lastKeys;

    private String address;
    private double balance;

    private Account(String address){

        this.address = address;
        this.balance = 0;
    }

    public String toString(){

        return this.address + ":" + this.balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAddress() {
        return address;
    }

    public static Account create(){

        lastKeys = crypto.keyPair();
        String address = "";

        try {
            address = new String(lastKeys.getPublic().getEncoded(), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new Account(address);
    }

    public static KeyPair popLastKeys(){

        KeyPair keys = lastKeys;
        lastKeys = null;
        return keys;
    }
}
