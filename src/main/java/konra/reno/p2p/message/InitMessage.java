package konra.reno.p2p.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import konra.reno.transaction.Transaction;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter @Setter
@Slf4j
public class InitMessage {

    MessageType type;
    Object payload;

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

    public static InitMessage create(MessageType type, Object payload) {

        InitMessage message = new InitMessage();
        message.setType(type);

        if(type == MessageType.BLOCK_REQUEST || type == MessageType.HEAD_INFO) {
            Long blockId = (Long) payload;
            message.setPayload(blockId);

        } else if(type == MessageType.TRANSACTION) {
            Transaction transaction = (Transaction) payload;
            message.setPayload(transaction.data());
        }

        return message;
    }
}

