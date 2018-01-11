package konra.reno.crypto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("crypto")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter @Setter
public class CryptoConfig {

    int keySize;
}
