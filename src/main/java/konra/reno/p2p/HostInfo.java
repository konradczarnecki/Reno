package konra.reno.p2p;

import lombok.*;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class HostInfo {

    @Getter private String address;
    @Getter private int port;
    @Getter @Setter private long headId;

    public static HostInfo createHostInfo(String hostAddress, P2PConfig config) {

        HostInfo info = new HostInfo();
        info.headId = -1;

        if(hostAddress.contains(":")) {
            String[] split = hostAddress.split(":");
            info.address = split[0];
            info.port = Integer.parseInt(split[1]);
        } else {
            info.address = hostAddress;
            info.port = config.getDefaultMessagePort();
        }

        return info;
    }
}
