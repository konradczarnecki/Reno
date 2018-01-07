package konra.reno.p2p;

import com.fasterxml.jackson.databind.ObjectMapper;
import konra.reno.blockchain.Block;
import konra.reno.blockchain.CoreService;
import konra.reno.p2p.handlers.BlockHandler;
import konra.reno.p2p.handlers.MessageHandler;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@Service
public class P2PService {

    private static final Logger log = LoggerFactory.getLogger(P2PService.class);

    private CoreService core;
    private P2PConfig config;

    @Resource
    private List<MessageHandler> handlers;

    private ServerSocketChannel incomingMessageSocket;
    private boolean online;
    private List<HostInfo> hosts;

    private ScheduledExecutorService exec;

    @Autowired
    public P2PService(CoreService core, P2PConfig config) {

        this.core = core;
        this.config = config;
    }

    @PostConstruct
    @SneakyThrows
    public void init() {

        core.registerSyncCallback(this::checkSync);
        exec = Executors.newScheduledThreadPool(10);
        online = false;

        incomingMessageSocket = ServerSocketChannel.open();
        incomingMessageSocket.socket().bind(new InetSocketAddress(config.getDefaultMessagePort()));
    }

    public void connect() {

        getHosts();
        checkSync();
        online = true;
        listenForMessages();
    }

    public void checkSync() {

        BlockHandler handler = (BlockHandler) handlers.stream().filter((hndl) -> hndl.getType() == InitMessage.Type.Block);
        handler.exchangeHeadInfo(hosts);
    }

    private void getHosts() {

        hosts = new ArrayList<>();
        String[] trackers = config.getTrackers().split(",");

        for(String tracker: trackers) {

            try {

                HostInfo info = HostInfo.createHostInfo(tracker, config);
                SocketChannel sc = SocketChannel.open(new InetSocketAddress(info.getAddress(), info.getPort()));

                ByteBuffer bb = ByteBuffer.allocate(2048);
                sc.read(bb);
                bb.flip();
                String[] addresses = new String(bb.array()).split(",");

                hosts.addAll(Arrays.stream(addresses).
                        map((address) -> HostInfo.createHostInfo(address, config)).
                        collect(Collectors.toList()));

                if(hosts.size() >= config.getInitHosts()) break;

            } catch (Exception ignored) {}
        }
    }

    @SneakyThrows
    public void listenForMessages() {

        Runnable listen = () -> {

            while (online) {
                log.debug("Listening...");
                SocketChannel sc = incomingMessageSocket.accept();
                processIncomingMessage(sc);
            }
        };
        exec.execute(listen);
    }

    @SneakyThrows
    private void processIncomingMessage(SocketChannel sc) {

        Runnable processMessage = () -> {

            ByteBuffer bf = ByteBuffer.allocate(2048);
            sc.read(bf);
            bf.flip();
            InitMessage message = InitMessage.parse(new String(bf.array()));
            log.debug("Incoming message " + message.toString());

            handlers.stream().filter(handler -> handler.getType() == message.getType()).
                    collect(Collectors.toList()).get(0).handleIncomingMessage(message, sc);
        };

        exec.execute(processMessage);
    }

    @SneakyThrows
    private List<Block> readBlocksFromSocket(SocketChannel sc) {

        ByteBuffer bf = ByteBuffer.allocate(Integer.BYTES);
        sc.read(bf);
        bf.flip();
        int size = bf.getInt();

        bf = ByteBuffer.allocate(size);
        sc.read(bf);
        bf.flip();

        String data = new String(bf.array());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(data, List.class);
    }

    @SneakyThrows
    private void writeBlocksToSocket(List<Block> blocks, SocketChannel sc) {

        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(blocks);

        ByteBuffer bf = ByteBuffer.allocate(Integer.BYTES);
        bf.putInt(data.getBytes().length);
        bf.flip();
        while(bf.hasRemaining()) sc.write(bf);

        bf = ByteBuffer.allocate(data.getBytes().length);
        bf.put(data.getBytes());
        bf.flip();
        while(bf.hasRemaining()) sc.write(bf);
    }
}
