package konra.reno;

import konra.reno.account.Account;
import konra.reno.core.block.Block;
import konra.reno.core.persistance.StateRepository;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StateRepositoryTest {

    @Autowired
    StateRepository repository;

    @Autowired
    MongoTemplate template;


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
