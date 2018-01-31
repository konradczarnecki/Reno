package konra.reno.core;

import konra.reno.core.block.Block;
import konra.reno.crypto.Crypto;
import konra.reno.transaction.Transaction;
import konra.reno.util.KeysDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.*;

@TestPropertySource(locations = "classpath:application-test.properties")
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class BlockTest {

    Block first;

    @Before
    public void init() {

        first = new Block(null);
    }


    @Test
    public void testBlockCreation() {

        Block b2 = new Block(first);

        assertEquals(first.getId(), b2.getId() - 1);
        assertEquals(first.getPow(), b2.getPreviousPOW());
    }

    @Test
    public void testHashing() {

        KeysDto keys = Crypto.keyPair();

        log.info(keys.getPublicKey() + "\n" + keys.getPrivateKey());

        Block b2 = new Block(first);
        Transaction t = new Transaction(keys.getPublicKey(), "receiver", 5, 5, "abc");
        Transaction t2 = new Transaction(keys.getPublicKey(), "other_receiver", 5, 5, "abc");

        t.sign(keys.getPrivateKey());
        t2.sign(keys.getPrivateKey());

        b2.setTransactions(new HashSet<>(Collections.singletonList(t)));
        String originalHash = b2.hash();

        b2.setTransactions(new HashSet<>(Collections.singletonList(t2)));
        String hashAfterChange = b2.hash();

        assertNotEquals(originalHash, hashAfterChange);
    }

    @Test
    public void testSerialization() {

        Block b2 = new Block(first);
        Transaction t = new Transaction("sender", "receiver", 5, 5, "abc");
        b2.setTransactions(new HashSet<>(Collections.singletonList(t)));

        String serialized = b2.data();

        Block deserialized = Block.parse(serialized);

        assertEquals(b2, deserialized);
    }

}
