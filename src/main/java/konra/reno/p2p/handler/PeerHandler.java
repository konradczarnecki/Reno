package konra.reno.p2p.handler;

import konra.reno.core.CoreService;
import konra.reno.p2p.HostInfo;
import konra.reno.p2p.P2PConfig;
import konra.reno.p2p.P2PService;
import konra.reno.p2p.message.InitMessage;
import konra.reno.p2p.message.MessageType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class PeerHandler implements MessageHandler {

    CoreService core;
    P2PConfig config;

    @Autowired
    public PeerHandler(CoreService core, P2PConfig config) {
        this.core = core;
        this.config = config;
    }

    @Override
    public boolean handleIncomingMessage(InitMessage message, SocketChannel sc) {

        boolean handled = false;

        switch (message.getType()) {

            case HEAD_INFO:
                handleBlockInfo(message, sc);
                handled = false;
                break;
        }

        return handled;
    }

    private void handleBlockInfo(InitMessage message, SocketChannel sc) {

        HostInfo host = HostInfo.createHostInfo(sc.socket().getInetAddress().getHostAddress(), config);
        host.setHeadId((Long) message.getPayload());
        P2PService.hosts().put(host.getAddress(), host);
    }

    public void getHosts() {

        String[] trackers = config.getTrackers().split(",");

        for(String tracker: trackers) {

            try {

                HostInfo info = HostInfo.createHostInfo(tracker, config);
                SocketChannel sc = SocketChannel.open(new InetSocketAddress(info.getAddress(), info.getPort()));

                ByteBuffer bb = ByteBuffer.allocate(2048);
                sc.read(bb);
                bb.flip();
                String[] addresses = new String(bb.array()).split(",");

                Arrays.stream(addresses)
                        .map(address -> HostInfo.createHostInfo(address, config))
                        .forEach(hostInfo -> P2PService.hosts().put(hostInfo.getAddress(), hostInfo));

                if(P2PService.hosts().size() >= config.getInitHosts()) break;

            } catch (Exception ignored) {}
        }
    }

    public HostInfo resolvePeerWithBlock(long blockId) {

        return P2PService.hosts().entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(hostInfo -> hostInfo.getHeadId() >= blockId)
                .min((host1, host2) -> (int) (host1.getHeadId() - host2.getHeadId())).get();
    }
}
