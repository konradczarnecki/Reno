package konra.reno.util;

import lombok.*;

import java.security.KeyPair;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
public class KeysDto {

    private String publicKey;
    private String privateKey;
}
