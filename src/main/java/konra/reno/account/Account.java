package konra.reno.account;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import konra.reno.crypto.Crypto;
import konra.reno.util.EncodingUtil;
import konra.reno.util.KeysDto;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "state")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Account {

    @Id
    String address;
    long balance;

    @Transient
    @JsonProperty
    KeysDto keys;

    public Account(String address){

        this.address = address;
        this.balance = 0;
    }

    public void add(long amount) {
        this.balance += amount;
    }

    public void subtract(long amount) {
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
