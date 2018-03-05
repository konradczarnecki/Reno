package konra.reno.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("core")
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CoreConfig {

    int maxTxsInPool;
    int baseBlockTime;
    String mode;
}
