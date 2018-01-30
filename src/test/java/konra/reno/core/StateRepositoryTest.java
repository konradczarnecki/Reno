package konra.reno.core;

import konra.reno.account.Account;
import konra.reno.core.block.Block;
import konra.reno.core.persistance.StateRepository;
import org.junit.Before;
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

    String accAddress;

    @Before
    public void init() {

        Account acc = Account.create();
        accAddress = acc.getAddress();

        repository.save(acc);
    }

    @Test
    public void testFindAccountByAddress() {

        Account acc = repository.findAccountByAddress(accAddress);
        assertNotNull(acc);
    }
}
