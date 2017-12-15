package konra.reno;

import static org.junit.Assert.*;

import konra.reno.blockchain.Block;
import konra.reno.blockchain.FileService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;

@SpringBootTest
public class TestFileService {

    FileService fileService;

    @Before
    public void init(){

        fileService = new FileService();
    }

    @Test
    public void testBlockchainReadWrite(){

        LinkedList<Block> blockchain = new LinkedList<>();
        Block initialBlock = new Block();
        Block secondBlock = new Block(initialBlock);

        blockchain.add(initialBlock);
        blockchain.add(secondBlock);

        fileService.writeBlockchain(blockchain);

        LinkedList<Block> fromFile = fileService.readBlockchain();

        assertNotNull(fromFile);
        assertEquals(fromFile.size(), 2);
    }
}
