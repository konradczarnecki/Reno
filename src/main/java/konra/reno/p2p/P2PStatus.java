package konra.reno.p2p;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class P2PStatus {

    int hostCount;
    int connectedHosts;
    boolean inSync;
    long headBlockId;
}
