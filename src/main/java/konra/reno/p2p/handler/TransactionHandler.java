package konra.reno.p2p.handler;

import konra.reno.blockchain.CoreService;
import konra.reno.p2p.message.InitMessage;
import konra.reno.p2p.message.MessageType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public TransactionHandler(CoreService core) {
        this.core = core;
        types = new HashSet<>(Collections.singletonList(MessageType.TRANSACTION));
    }

    @Override
    public void handleIncomingMessage(InitMessage message, SocketChannel sc) {

    }
}