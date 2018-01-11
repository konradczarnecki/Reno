package konra.reno.p2p;

import konra.reno.core.Block;
import konra.reno.core.CoreService;
import konra.reno.p2p.handler.BlockHandler;
import konra.reno.p2p.handler.MessageHandler;
import konra.reno.p2p.handler.PeerHandler;
import konra.reno.p2p.message.InitMessage;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class P2PService {

    private static Map<String, HostInfo> hosts;

    static {
        hosts = new HashMap<>();
    }

    CoreService core;
    P2PConfig config;

    @Resource
    List<MessageHandler> handlers;
    ScheduledExecutorService exec;

    ServerSocketChannel incomingMessageSocket;
    boolean doConnect;
    boolean doSync;
    boolean bulkDownload;

    @Autowired
    public P2PService(CoreService core, P2PConfig config) {
        this.core = core;
        this.config = config;
    }

    @PostConstruct
    @SneakyThrows
    public void init() {

        core.registerSyncCallback(this::refreshHeadInfo);
        exec = Executors.newScheduledThreadPool(10);
        doConnect = false;
        doSync = false;
        bulkDownload = false;

        incomingMessageSocket = ServerSocketChannel.open();
        incomingMessageSocket.socket().bind(new InetSocketAddress(config.getDefaultMessagePort()));
    }

    public void connect() {

        doConnect = true;
        exec.execute(this::listenForMessages);
        exec.execute(this::getHosts);
    }

    public void disconnect() {

        doConnect = false;
        doSync = false;
        exec.shutdownNow();
        exec = Executors.newScheduledThreadPool(10);
    }

    public Status checkConnect() {

        Status status = new Status();
        status.setHostCount(hosts.size());

        int connectedHosts = (int) hosts.values().stream()
                .filter(host -> host.getHeadId() != -1).count();

        status.setConnectedHosts(connectedHosts);

        return status;
    }

    public void synchronize() {

        doSync = true;
        Status status = checkConnect();

        if(status.getConnectedHosts() == 0 || status.getHostCount() == 0)
            throw new IllegalStateException("No connected hosts to synchronize with.");

        exec.execute(this::sync);
    }

    private void sync() {

        if(checkSync()) return;

        BlockHandler blockHandler = (BlockHandler) getHandler(BlockHandler.class);
        PeerHandler peerHandler = (PeerHandler) getHandler(PeerHandler.class);

        HostInfo host = peerHandler.resolvePeerWithBlock(core.getHeadBlockId() + 1);
        List<Block> blocks = blockHandler.requestBlocks(host);
        core.processIncomingBlocks(blocks);

        if(doSync) sync();
    }

    public boolean checkSync() {

        if(!bulkDownload) refreshHeadInfo();

        HostInfo headHost = hosts.values().stream()
                .filter(host -> host.getHeadId() != -1)
                .max((host1, host2) -> (int) (host1.getHeadId() - host2.getHeadId())).get();

        core.setNetworkHead(headHost.getHeadId());
        return headHost.getHeadId() <= core.getHeadBlockId();
    }

    public void refreshHeadInfo() {

        BlockHandler handler = (BlockHandler) getHandler(BlockHandler.class);
        handler.exchangeHeadBlockInfo();
    }

    private void getHosts() {

        PeerHandler handler = (PeerHandler) getHandler(PeerHandler.class);
        handler.getHosts(hosts, config);
    }

    @SneakyThrows
    private void listenForMessages() {

        while (doConnect) {
            log.debug("Listening...");
            SocketChannel sc = incomingMessageSocket.accept();
            processIncomingMessage(sc);
        }
    }

    @SneakyThrows
    private void processIncomingMessage(SocketChannel sc) {

        Runnable processMessage = () -> {

            ByteBuffer bf = ByteBuffer.allocate(2048);
            sc.read(bf);
            bf.flip();
            InitMessage message = InitMessage.parse(new String(bf.array()));
            log.debug("Incoming message " + message.toString());

            handlers.stream()
                    .filter(handler -> handler.canHandle(message.getType()))
                    .forEach(handler -> handler.handleIncomingMessage(message, sc));
        };

        exec.execute(processMessage);
    }

    public MessageHandler getHandler(Class handlerClass) {

        return handlers.stream()
                .filter(handler -> handler.getClass() == handlerClass)
                .findFirst().get();
    }

    public static Map<String, HostInfo> hosts() {
        return hosts;
    }
}
