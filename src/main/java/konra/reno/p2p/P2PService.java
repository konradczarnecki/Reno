package konra.reno.p2p;

import konra.reno.blockchain.CoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Value("${main.port}")
    private int chainSyncPort;

    @Value("${test.hb}")
    private int headBlock;

    @Value("${test.hosts}")
    private String testHosts;
    private ServerSocketChannel chainSyncSocket;
    private boolean listenForSync;
    private List<String> hosts;

    private ScheduledExecutorService exec;

    public  P2PService(){

    }

    @PostConstruct
    public void init() {

        exec = Executors.newScheduledThreadPool(10);
        listenForSync = false;

        try {
            chainSyncSocket = ServerSocketChannel.open();
            chainSyncSocket.socket().bind(new InetSocketAddress(chainSyncPort));

        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("head: " + headBlock);
        log.info(chainSyncPort + "");


        String[] hs = testHosts.split(",");
        hosts = new ArrayList<>();
        hosts.addAll(Arrays.asList(hs));
    }

    public void runSyncProcess(){

        listenForSync = true;

        Runnable listenForHeadBlock = () -> {

            try {

                while(listenForSync){
                    log.info("listening");
                    SocketChannel sc = chainSyncSocket.accept();
                    log.info("incoming request");
                    processIncomingSyncMessage(sc);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Runnable sendHeadBlock = () -> {

            try {

                for(String host: hosts){

                    log.info("sending to host " + host);

                    String[] split = host.split(":");
                    String hostname = split[0];
                    Integer port = Integer.parseInt(split[1]);

                    SocketChannel sc = SocketChannel.open(new InetSocketAddress(hostname, port));

                    log.info("socket opened");
                    log.info(sc.isConnected() + "");

                    ByteBuffer bf = ByteBuffer.allocate(Long.BYTES);
                    bf.putLong(headBlock);
                    bf.flip();

                    while(bf.hasRemaining()) sc.write(bf);

                    log.info("head written");

                    bf.clear();
                    sc.read(bf);
                    bf.flip();

                    Long peerHeadBlock = bf.getLong();

                    log.info("Peer head: " + peerHeadBlock);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        exec.execute(listenForHeadBlock);
        exec.scheduleAtFixedRate(sendHeadBlock, 5000, 5000, TimeUnit.MILLISECONDS);
    }

    public void processIncomingSyncMessage(SocketChannel sc){

        Runnable processRequest = () -> {

            try {
                ByteBuffer bf = ByteBuffer.allocate(Long.BYTES);
                int bytesRead = sc.read(bf);
//                if(bytesRead != -1) throw new RuntimeException("Chuj");
                log.info("bytes read " + bytesRead);
                bf.flip();
                long peerHeadBlock = bf.getLong();
                log.info(peerHeadBlock + "");

                bf.clear();
                bf.putLong(headBlock);
                bf.flip();

                while(bf.hasRemaining()) sc.write(bf);
                bf.clear();

                log.info("return head written");

                if(headBlock > peerHeadBlock){
                    log.info("Sending blockchain ");
                } else if(headBlock < peerHeadBlock) {
                    log.info("Receiving blockchain");
                } else {
                    log.info("In Sync");
                }


            }catch(Exception e){
                e.printStackTrace();
            }
        };

        exec.execute(processRequest);
    }
}
