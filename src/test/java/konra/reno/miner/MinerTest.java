package konra.reno.miner;

import konra.reno.account.Account;
import konra.reno.core.CoreService;
import konra.reno.core.persistance.StateRepository;
import konra.reno.miner.txpicker.TxPickerConfig;
import konra.reno.transaction.Transaction;
import konra.reno.util.KeysDto;
import lombok.extern.slf4j.Slf4j;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
//@TestPropertySource("classpath:application-test.properties")
//@EnableConfigurationProperties(TxPickerConfig.class)
@Slf4j
public class MinerTest {

    @Autowired
    MinerService service;

    @Autowired
    CoreService core;

    @Autowired
    StateRepository stateRepository;

    ScheduledExecutorService exec;
    Account miner;
    Account reciever;

    @Before
    public void init() {

        exec = Executors.newScheduledThreadPool(8);
        reciever = Account.create();
        miner = Account.create();
    }

    @Test
    public void testMining() throws InterruptedException {

        core.startBlockchain();

        service.startMining(miner.getAddress(), "dupaa");
        exec.schedule(this::addTransaction, 5000, TimeUnit.MILLISECONDS);
        Thread.sleep(10000);

        service.stopMining();

        Account rcv = stateRepository.findAccountByAddress(reciever.getAddress());
        assertEquals(20, rcv.getBalance());
    }

    private void addTransaction() {

        log.info("adding tx");
        Transaction t = new Transaction(miner.getAddress(), reciever.getAddress(), 20, 5, "xd");
        t.sign(miner.getKeys().getPrivateKey());
        core.addNewTransaction(t);
    }
}
