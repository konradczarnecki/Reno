package konra.reno.p2p;

import com.fasterxml.jackson.databind.ObjectMapper;
import konra.reno.blockchain.Block;
import konra.reno.transaction.Transaction;
import lombok.*;

@ToString
@NoArgsConstructor
public class InitMessage {

    public enum Type { Transaction, Block }

    @Getter @Setter private Type type;
    @Getter @Setter private long transactionSize;
    @Getter @Setter private long blockId;

    public static InitMessage transactionMessage(Transaction transaction) {

        InitMessage message = new InitMessage();
        message.type = Type.Transaction;
        message.transactionSize = transaction.data().getBytes().length;
        return message;
    }

    public static InitMessage BlockMessage(long blockId) {

        InitMessage message = new InitMessage();
        message.type = Type.Block;
        message.blockId = blockId;
        return message;
    }

    @SneakyThrows
    public String data() {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    @SneakyThrows
    public static InitMessage parse(String data) {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(data, InitMessage.class);
    }
}
