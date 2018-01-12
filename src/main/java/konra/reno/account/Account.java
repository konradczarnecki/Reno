package konra.reno.account;

import konra.reno.crypto.Crypto;
import konra.reno.util.KeysDto;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
@Builder
@Document(collection = "state")
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Getter @Setter
public class Account {

    @Id
    @Field("address") String address;
    @Field("balance") double balance;

    @Transient
    KeysDto keys;

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

        KeysDto keys = Crypto.keyPair();

        return builder()
                .address(keys.getPublicKey())
                .balance(0)
                .keys(keys)
                .build();
    }
}
