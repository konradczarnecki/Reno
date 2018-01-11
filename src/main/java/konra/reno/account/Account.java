package konra.reno.account;

import konra.reno.crypto.Crypto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.xml.bind.DatatypeConverter;
import java.security.*;

@Document(collection = "state")
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter @Setter
public class Account {

    private static KeyPair lastKeys;

    @Id
    @Field("address") private String address;
    @Field("balance") private double balance;

    public Account(String address){

        this.address = address;
        this.balance = 0;
    }

    public void add(double amount) {
        this.balance += amount;
    }

    public void subtract(double amount) {
        this.balance -= amount;
    }

    @SneakyThrows
    public static Account create(){

        lastKeys = Crypto.keyPair();
        String address = DatatypeConverter.printHexBinary(lastKeys.getPublic().getEncoded());
        return new Account(address);
    }

    public static KeyPair popLastKeys(){

        KeyPair keys = lastKeys;
        lastKeys = null;
        return keys;
    }

}
