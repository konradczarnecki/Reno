package konra.reno.p2p.handler;

import konra.reno.core.CoreService;
import konra.reno.core.callback.CallbackHandler;
import konra.reno.core.callback.CallbackType;
import konra.reno.p2p.P2PService;
import konra.reno.p2p.message.InitMessage;
import konra.reno.p2p.message.MessageType;
import konra.reno.transaction.Transaction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bson.ByteBuf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class TransactionHandler implements MessageHandler {

    @Getter Set<MessageType> types;
    CoreService core;

    @Autowired
    public TransactionHandler(CoreService core, CallbackHandler callbackHandler) {

        this.core = core;
        types = new HashSet<>(Collections.singletonList(MessageType.TRANSACTION));
        callbackHandler.register(CallbackType.TRANSACTION, this::announceTransaction);
    }

    @Override
    public void handleIncomingMessage(InitMessage message, SocketChannel sc) {

        Transaction transaction = Transaction.parse(message.data());
        core.getTransactionPool().addToPool(transaction);
        if(!core.getTransactionPool().checkIfPending(transaction)) announceTransaction(transaction);
    }

    public void announceTransaction(Object transaction) {

        String message = InitMessage.create(MessageType.TRANSACTION, transaction).data();

        ByteBuffer bb = ByteBuffer.allocate(message.getBytes().length);
        bb.put(message.getBytes());
        bb.flip();

        P2PService.hosts().values().forEach(host -> {

            try {

                SocketChannel sc = SocketChannel.open(new InetSocketAddress(host.getAddress(), host.getPort()));
                while(bb.hasRemaining()) sc.write(bb);
                bb.flip();

            } catch (Exception ignored) { }

        });
    }
}
