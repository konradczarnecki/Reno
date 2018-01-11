package konra.reno;

import konra.reno.account.Account;
import konra.reno.core.Block;
import konra.reno.core.persistance.StateRepository;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.bind.DatatypeConverter;
import java.security.KeyPair;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StateRepositoryTest {

    @Autowired
    StateRepository repository;

    @Autowired
    MongoTemplate template;

    @Test
    public void test() {

        Account ac = Account.create();
        KeyPair keys = Account.popLastKeys();

        ac.setBalance(50);

        repository.save(ac);

        Account ac2 = repository.getAccountByAddress(DatatypeConverter.printHexBinary(keys.getPublic().getEncoded()));

        assertEquals(ac, ac2);
    }

    @Test
    public void testRead() {

        List<Account> ac = repository.getAllByAddressNotNull();

        assertEquals(ac.size(), 1);
    }

    @Test
    public void testTemplate() {

        if(!template.collectionExists("chunk_846")) template.createCollection("chunk_846");
        assertTrue(template.collectionExists("chunk_846"));

        Account ac = Account.create();
        Block b = new Block(null);
        Block b2 = new Block(b);

        template.save(b, "chunk_846");

    }
}
