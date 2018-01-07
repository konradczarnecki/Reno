package konra.reno.p2p.handlers;

import konra.reno.p2p.InitMessage;

import java.nio.channels.SocketChannel;

public interface MessageHandler {

    void handleIncomingMessage(InitMessage message, SocketChannel sc);
    InitMessage.Type getType();
}
