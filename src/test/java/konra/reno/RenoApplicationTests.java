package konra.reno;

import konra.reno.blockchain.Block;
import konra.reno.blockchain.FileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RenoApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(RenoApplication.class);

	@Test
	public void contextLoads() {
	}

}
