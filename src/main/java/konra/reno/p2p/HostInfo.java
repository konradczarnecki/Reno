package konra.reno.p2p;

import lombok.*;
import lombok.experimental.FieldDefaults;

@ToString
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HostInfo {

    String address;
    int port;
    @Setter long headId;

    private HostInfo() {}

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

    public boolean equals(Object o) {

        if(this == o) return true;
        if(o == null || o.getClass() != getClass()) return false;
        HostInfo other = (HostInfo) o;

        return this.address.equals(other.address) && this.port == other.port;
    }
}
