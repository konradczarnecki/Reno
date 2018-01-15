package konra.reno.p2p.handler;

import konra.reno.p2p.message.InitMessage;

import java.nio.channels.SocketChannel;

public interface MessageHandler {

    boolean handleIncomingMessage(InitMessage message, SocketChannel sc);
}
