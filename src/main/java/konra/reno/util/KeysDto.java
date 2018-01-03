package konra.reno.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.KeyPair;

@NoArgsConstructor
public class KeysDto {

    @Getter @Setter private String publicKey;
    @Getter @Setter private String privateKey;

    public KeysDto(String publicKey, String privateKey){

        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public KeysDto(KeyPair keys){

        try {

            this.publicKey = new String(keys.getPublic().getEncoded(), "UTF-8");
            this.privateKey = new String(keys.getPrivate().getEncoded(), "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
