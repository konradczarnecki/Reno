package konra.reno.p2p.handlers;

import konra.reno.blockchain.CoreService;
import konra.reno.p2p.HostInfo;
import konra.reno.p2p.InitMessage;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

@Service
public class BlockHandler implements MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(BlockHandler.class);

    @Getter private InitMessage.Type type;
    private CoreService core;

    @Autowired
    public BlockHandler(CoreService core) {
        this.core = core;
        type = InitMessage.Type.Block;
    }

    @SneakyThrows
    public void exchangeHeadInfo(List<HostInfo> hosts) {

        String message = InitMessage.BlockMessage(core.getHeadBlockId()).data();

        for (HostInfo host : hosts) {

            log.debug("Sending message to host " + host.getAddress());

            SocketChannel sc = SocketChannel.open(new InetSocketAddress(host.getAddress(), host.getPort()));
            log.debug("Socket opened");

            ByteBuffer bf = ByteBuffer.allocate(message.getBytes().length);
            bf.put(message.getBytes());
            bf.flip();
            while (bf.hasRemaining()) sc.write(bf);
            log.debug("Head init message sent.");

            bf.clear();
            sc.read(bf);
            bf.flip();

            InitMessage returnMessage = InitMessage.parse(new String(bf.array()));
            host.setHeadId(returnMessage.getBlockId());
            log.debug("Peer head: " + host.getHeadId());
        }
    }

    @Override
    public void handleIncomingMessage(InitMessage message, SocketChannel sc) {

    }
}
