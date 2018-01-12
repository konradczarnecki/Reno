package konra.reno.p2p;

import konra.reno.p2p.message.InitMessage;
import konra.reno.p2p.message.MessageType;
import konra.reno.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class InitMessageTest {

    @Test
    public void testMessageSerialization() {

        Transaction t = new Transaction("a", "b", 5, 5);
        InitMessage message = InitMessage.create(MessageType.TRANSACTION, t);
        String data = message.data();
        log.info(data);

        InitMessage parsed = InitMessage.parse(data);

        assertEquals(parsed, message);
    }
}
