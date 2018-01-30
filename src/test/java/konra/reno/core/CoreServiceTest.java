package konra.reno.core;

import konra.reno.account.Account;
import konra.reno.core.block.Block;
import konra.reno.core.block.BlockConfiguration;
import konra.reno.core.callback.CallbackHandler;
import konra.reno.core.callback.CallbackType;
import konra.reno.core.persistance.BlockRepository;
import konra.reno.core.persistance.StateRepository;
import konra.reno.transaction.Transaction;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@TestPropertySource(locations = "classpath:application-test.properties")
@RunWith(SpringRunner.class)
@SpringBootTest
public class CoreServiceTest {

    private static String dbPath = "";

    @Autowired BlockRepository blockRepository;
    @Autowired StateRepository stateRepository;
    @Autowired CoreConfig config;

    @Mock BlockConfiguration blockConfiguration;

    CoreService core;

    @Value("${spring.mongodb.embedded.storage.database-dir}")
    String testMongoPath;

    @Autowired MongoTemplate template;

    @Before
    public void init() {

        dbPath = testMongoPath;
        template.remove(new Query(), "blockchain");
        template.remove(new Query(), "state");

        when(blockConfiguration.getDifficulty(Matchers.anyObject())).thenReturn(0);
        when(blockConfiguration.getReward(Matchers.anyObject())).thenReturn(10L);

        core = new CoreService(
                blockRepository,
                stateRepository,
                blockConfiguration,
                config,
                new MockCallbackHandler()
        );

        Account miner1 = new Account("aa");
        miner1.setBalance(20);
        stateRepository.save(miner1);

        Account miner2 = new Account("bb");
        miner2.setBalance(10);
        stateRepository.save(miner2);

        Block first = new Block(null);
        first.setPow("pow1");
        first.setMiner(miner1.getAddress());

        Block second = new Block(first);
        second.setPow("pow2");
        second.setMiner(miner1.getAddress());

        Block third = new Block(second);
        third.setPow("pow3");
        third.setMiner(miner2.getAddress());

        blockRepository.save(Arrays.asList(first, second, third));
        core.setHeadBlock(third);
    }

    @AfterClass
    public static void cleanUp() throws IOException {

        Path path = Paths.get(dbPath);
        Files.walk(path, FileVisitOption.FOLLOW_LINKS)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    public void testProcessNewBlocks() {

        receiveBlocks();

        assertEquals(5, core.getHeadBlock().getId());
        assertEquals(5, blockRepository.findTopByOrderByIdDesc().getId());

        Account miner1 = stateRepository.findAccountByAddress("aa");
        Account miner2 = stateRepository.findAccountByAddress("bb");
        Account miner3 = stateRepository.findAccountByAddress("cc");
        Account receiver = stateRepository.findAccountByAddress("dd");

        assertEquals(13, miner1.getBalance());
        assertEquals(15, miner2.getBalance());
        assertEquals(14, miner3.getBalance());
        assertEquals(8, receiver.getBalance());
    }

    @Test
    public void testRollbackBlocks() {

        receiveBlocks();
        core.rollbackFromBlock(4);

        assertEquals(3, core.getHeadBlock().getId());

        Account miner1 = stateRepository.findAccountByAddress("aa");
        Account miner2 = stateRepository.findAccountByAddress("bb");
        Account miner3 = stateRepository.findAccountByAddress("cc");
        Account receiver = stateRepository.findAccountByAddress("dd");

        assertEquals(20, miner1.getBalance());
        assertEquals(10, miner2.getBalance());
        assertEquals(0, miner3.getBalance());
        assertEquals(0, receiver.getBalance());
    }

    private void receiveBlocks() {

        Transaction tx = new Transaction("aa", "dd", 5, 2, "");
        Transaction tx2 = new Transaction("bb", "dd", 3, 2, "");
        Transaction spyTx1 = spy(tx);
        Transaction spyTx2 = spy(tx2);

        doReturn(true).when(spyTx1).validate();
        doReturn(true).when(spyTx2).validate();

        Block fourth = new Block(core.getHeadBlock());
        fourth.setPow("pow4");
        fourth.setMiner("cc");
        fourth.setTransactions(new HashSet<>(Arrays.asList(spyTx1, spyTx2)));

        Block fifth = new Block(fourth);
        fifth.setPow("pow5");
        fifth.setMiner("bb");
        fifth.setTransactions(new HashSet<>());

        Block spyBlock4 = spy(fourth);
        Block spyBlock5 = spy(fifth);

        doReturn(true).when(spyBlock4).validate(0);
        doReturn(true).when(spyBlock5).validate(0);

        List<Block> newBlocks = Arrays.asList(spyBlock4, spyBlock5);
        core.processNewBlocks(newBlocks);
    }

    class MockCallbackHandler extends CallbackHandler {

        List<CallbackType> executedCallbacks;

        MockCallbackHandler() {

            super();
            executedCallbacks = new ArrayList<>();
        }

        public void execute(CallbackType type) {
            executedCallbacks.add(type);
        }

        public void execute(CallbackType type, Object payload) {
            executedCallbacks.add(type);
        }
    }
}
