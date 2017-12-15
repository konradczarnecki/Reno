package konra.reno;

import konra.reno.blockchain.Block;
import konra.reno.blockchain.FileService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;

@SpringBootTest
public class CoreServiceTest {

    private static final Logger log = LoggerFactory.getLogger(CoreServiceTest.class);

    @Test
    public void checkBlockchain(){

        FileService fs = new FileService();
        LinkedList<Block> blockain = fs.readBlockchain();

        for(Block b: blockain){

            log.info("Block {}", b.toString());
        }
    }

}
