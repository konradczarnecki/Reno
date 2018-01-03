package konra.reno;

import static org.junit.Assert.*;

import konra.reno.blockchain.Block;
import konra.reno.util.FileService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;

@SpringBootTest
public class TestFileService {

    @Autowired
    FileService fileService;

    @Before
    public void init(){

    }

    @Test
    public void testChunkWrite() {


    }
}
