package konra.reno.p2p.handlers;

import konra.reno.blockchain.CoreService;
import konra.reno.p2p.InitMessage;
import konra.reno.p2p.handlers.MessageHandler;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.channels.SocketChannel;

@Service
public class TransactionHandler implements MessageHandler {

    @Getter private InitMessage.Type type;
    private CoreService core;

    @Autowired
    public TransactionHandler(CoreService core) {
        this.core = core;
        type = InitMessage.Type.Transaction;
    }

    @Override
    public void handleIncomingMessage(InitMessage message, SocketChannel sc) {

    }
}
