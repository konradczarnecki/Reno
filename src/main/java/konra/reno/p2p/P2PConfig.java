package konra.reno.p2p;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("p2p")
public class P2PConfig {

    @Getter @Setter private String trackers;
    @Getter @Setter private int defaultMessagePort;
    @Getter @Setter private int initHosts;
    @Getter @Setter private int maxHosts;
}
