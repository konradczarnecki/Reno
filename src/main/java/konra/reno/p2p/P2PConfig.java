package konra.reno.p2p;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("p2p")
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class P2PConfig {

    String trackers;
    int defaultMessagePort;
    int initHosts;
    int maxHosts;
    int maxBlocksToTransfer;
}
