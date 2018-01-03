package konra.reno.p2p;

import konra.reno.blockchain.CoreService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class P2PService {

    private static final Logger log = LoggerFactory.getLogger(P2PService.class);

    private CoreService core;

    @Value("${main.port}")
    private int chainSyncPort;

    @Value("${test.hosts}")
    private String testHosts;
    private ServerSocketChannel chainSyncSocket;
    private boolean listenForSync;
    private List<String> hosts;

    private ScheduledExecutorService exec;

    @Autowired
    public P2PService(CoreService core) {

        this.core = core;
    }

    @PostConstruct
    @SneakyThrows
    public void init() {

        exec = Executors.newScheduledThreadPool(10);
        listenForSync = false;

        chainSyncSocket = ServerSocketChannel.open();
        chainSyncSocket.socket().bind(new InetSocketAddress(chainSyncPort));

        log.debug(chainSyncPort + "");

        String[] hs = testHosts.split(",");
        hosts = new ArrayList<>();
        hosts.addAll(Arrays.asList(hs));
    }

    @SneakyThrows
    public void runSyncProcess() {

        listenForSync = true;

        Runnable listenForHeadBlock = () -> {

            while (listenForSync) {
                log.debug("listening");
                SocketChannel sc = chainSyncSocket.accept();
                log.debug("incoming request");
                processIncomingSyncMessage(sc);
            }
        };

        Runnable sendHeadBlock = () -> {

            for (String host : hosts) {

                log.debug("sending to host " + host);

                String[] split = host.split(":");
                String hostname = split[0];
                Integer port = Integer.parseInt(split[1]);

                SocketChannel sc = SocketChannel.open(new InetSocketAddress(hostname, port));

                log.debug("socket opened");
                log.debug(sc.isConnected() + "");

                ByteBuffer bf = ByteBuffer.allocate(Long.BYTES);
                bf.putLong(core.getHeadBlock());
                bf.flip();

                while (bf.hasRemaining()) sc.write(bf);

                log.debug("head written");

                bf.clear();
                sc.read(bf);
                bf.flip();

                Long peerHeadBlock = bf.getLong();

                log.debug("Peer head: " + peerHeadBlock);

            }
        };

        exec.execute(listenForHeadBlock);
        exec.scheduleAtFixedRate(sendHeadBlock, 5000, 5000, TimeUnit.MILLISECONDS);
    }

    @SneakyThrows
    private void processIncomingSyncMessage(SocketChannel sc) {

        Runnable processRequest = () -> {

            ByteBuffer bf = ByteBuffer.allocate(Long.BYTES);
            sc.read(bf);
            bf.flip();
            long peerHeadBlock = bf.getLong();

            log.info(peerHeadBlock + "");

            bf.clear();
            bf.putLong(core.getHeadBlock());
            bf.flip();

            while (bf.hasRemaining()) sc.write(bf);
            bf.clear();

            log.info("return head written");

            if (core.getHeadBlock() > peerHeadBlock) {
                log.info("Sending blockchain ");
            } else if (core.getHeadBlock() < peerHeadBlock) {
                log.info("Receiving blockchain");
            } else {
                log.info("In Sync");
            }
        };

        exec.execute(processRequest);
    }
}
