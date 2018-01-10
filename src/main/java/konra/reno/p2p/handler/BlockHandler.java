package konra.reno.p2p.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import konra.reno.blockchain.Block;
import konra.reno.blockchain.CoreService;
import konra.reno.p2p.HostInfo;
import konra.reno.p2p.P2PService;
import konra.reno.p2p.message.InitMessage;
import konra.reno.p2p.message.MessageType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
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
public class BlockHandler implements MessageHandler {

    @Getter Set<MessageType> types;
    CoreService core;

    @Autowired
    public BlockHandler(CoreService core) {
        this.core = core;
        types = new HashSet<>(Arrays.asList(MessageType.BLOCK_REQUEST, MessageType.HEAD_INFO));
    }

    @Override
    public void handleIncomingMessage(InitMessage message, SocketChannel sc) {

        if(message.getType() == MessageType.HEAD_INFO) handleBlockInfo(message, sc);
        else if(message.getType() == MessageType.BLOCK_REQUEST) handleBlockRequest(message, sc);
    }

    @SneakyThrows
    private void handleBlockInfo(InitMessage message, SocketChannel sc) {

       String response = InitMessage.create(MessageType.HEAD_INFO, core.getHeadBlockId()).data();
       ByteBuffer bb = ByteBuffer.allocate(response.getBytes().length);
       bb.put(response.getBytes());
       bb.flip();
       while(bb.hasRemaining()) sc.write(bb);
       sc.close();
    }

    @SneakyThrows
    public void exchangeHeadBlockInfo() {

        String message = InitMessage.create(MessageType.HEAD_INFO, core.getHeadBlockId()).data();

        for (HostInfo host : P2PService.hosts().values()) {

            try {
                log.debug("Sending message to host " + host.getAddress());

                SocketChannel sc = SocketChannel.open(new InetSocketAddress(host.getAddress(), host.getPort()));
                log.debug("Socket opened");

                ByteBuffer bf = ByteBuffer.allocate(1024);
                bf.put(message.getBytes());
                bf.flip();
                while (bf.hasRemaining()) sc.write(bf);
                log.debug("Head init message sent.");

                bf.clear();
                sc.read(bf);
                bf.flip();

                InitMessage returnMessage = InitMessage.parse(new String(bf.array()));
                host.setHeadId((Long) returnMessage.getPayload());
                log.debug("Peer head: " + host.getHeadId());

            } catch (Exception ignored) {}
        }
    }

    private void handleBlockRequest(InitMessage message, SocketChannel sc) {


    }

    @SneakyThrows
    public void requestBlocks(HostInfo host) {

        String message = InitMessage.create(MessageType.BLOCK_REQUEST, core.getHeadBlockId()).data();

        SocketChannel sc = SocketChannel.open(new InetSocketAddress(host.getAddress(), host.getPort()));
        log.debug("Socket opened");

        ByteBuffer bf = ByteBuffer.allocate(1024);
        bf.put(message.getBytes());
        bf.flip();
        while (bf.hasRemaining()) sc.write(bf);
        log.debug("Block request message sent.");

        List<Block> blocks = readBlocksFromSocket(sc);


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
