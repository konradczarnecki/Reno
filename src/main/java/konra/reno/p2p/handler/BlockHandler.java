package konra.reno.p2p.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import konra.reno.core.block.Block;
import konra.reno.core.CoreService;
import konra.reno.core.callback.CallbackType;
import konra.reno.p2p.HostInfo;
import konra.reno.p2p.P2PConfig;
import konra.reno.p2p.P2PService;
import konra.reno.p2p.message.InitMessage;
import konra.reno.p2p.message.MessageType;
import lombok.AccessLevel;
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

    CoreService core;
    P2PConfig config;

    @Autowired
    public BlockHandler(CoreService core, P2PConfig config) {
        this.core = core;
        this.config = config;
        core.getCallbackHandler().register(CallbackType.HEAD_EXCHANGE, this::exchangeHeadBlockInfo);
    }

    @Override
    public boolean handleIncomingMessage(InitMessage message, SocketChannel sc) {

        boolean handled = false;

        switch (message.getType()) {

            case HEAD_INFO:
                handleBlockInfo(message, sc);
                handled = true;
                break;
            case BLOCK_REQUEST:
                handleBlockRequest(message, sc);
                handled = true;
                break;
        }
        return handled;
    }

    @SneakyThrows
    private void handleBlockInfo(InitMessage message, SocketChannel sc) {

       String response = InitMessage.create(MessageType.HEAD_INFO, core.getHeadBlock().getId()).data();
       ByteBuffer bb = ByteBuffer.allocate(response.getBytes().length);
       bb.put(response.getBytes());
       bb.flip();
       while(bb.hasRemaining()) sc.write(bb);
       sc.close();
    }

    @SneakyThrows
    public void exchangeHeadBlockInfo() {

        String message = InitMessage.create(MessageType.HEAD_INFO, core.getHeadBlock().getId()).data();

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

        long requestBlockId = (Long) message.getPayload();
        long headDifference = core.getHeadBlock().getId() - requestBlockId;
        int blocksToSend = (int) (headDifference < config.getMaxBlocksPerTransfer() ? headDifference : config.getMaxBlocksPerTransfer());

        List<Block> blocks = core.getBlocks(requestBlockId, blocksToSend);
        writeBlocksToSocket(blocks, sc);
    }

    @SneakyThrows
    public List<Block> requestBlocks(HostInfo host) {

        String message = InitMessage.create(MessageType.BLOCK_REQUEST, core.getHeadBlock().getId()).data();

        SocketChannel sc = SocketChannel.open(new InetSocketAddress(host.getAddress(), host.getPort()));
        log.debug("Socket opened");

        ByteBuffer bf = ByteBuffer.allocate(1024);
        bf.put(message.getBytes());
        bf.flip();
        while (bf.hasRemaining()) sc.write(bf);
        log.debug("Block request message sent.");

        return readBlocksFromSocket(sc);
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
