package konra.reno.p2p.handler;

import konra.reno.p2p.message.InitMessage;
import konra.reno.p2p.message.MessageType;

import java.nio.channels.SocketChannel;
import java.util.Set;

public interface MessageHandler {

    Set<MessageType> getTypes();
    void handleIncomingMessage(InitMessage message, SocketChannel sc);

    default boolean canHandle(MessageType type) {
        return getTypes().stream().anyMatch(handlerType -> handlerType == type);
    }
}
