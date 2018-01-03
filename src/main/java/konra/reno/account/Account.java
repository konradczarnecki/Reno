package konra.reno.account;

import konra.reno.util.Crypto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.*;

@Document(collection = "state")
@NoArgsConstructor
@EqualsAndHashCode
public class Account {

    private static KeyPair lastKeys;

    @Id
    @Field("address")
    @Getter @Setter private String address;

    @Field("balance")
    @Getter @Setter private double balance;

    private Account(String address){

        this.address = address;
        this.balance = 0;
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

    public String toString(){

        return this.address + ":" + this.balance;
    }
}
