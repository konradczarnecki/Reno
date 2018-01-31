package konra.reno.miner;

import konra.reno.account.Account;
import konra.reno.core.CoreService;
import konra.reno.util.KeysDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MinerTest {

    @Autowired
    MinerService service;

    @Autowired
    CoreService core;

    @Test
    public void testMining() throws InterruptedException {

        core.startBlockchain();

        Account miner = Account.create();
        service.startMining(miner.getAddress(), "dupaa");

        Thread.sleep(600000);
    }
}
