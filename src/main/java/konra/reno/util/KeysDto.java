package konra.reno.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.KeyPair;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class KeysDto {

    private String publicKey;
    private String privateKey;
}
