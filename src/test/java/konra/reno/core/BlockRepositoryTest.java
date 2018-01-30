package konra.reno.core;

import konra.reno.core.block.Block;
import konra.reno.core.persistance.BlockRepository;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlockRepositoryTest {

    @Autowired
    BlockRepository repository;

    @Before
    public void init() {

        Block last = new Block(null);
        repository.save(last);

        for(int i = 0; i < 10; i++) {
            last = new Block(last);
            repository.save(last);
        }
    }

    @Test
    public void testFindBlockById() {

        Block b = repository.findBlockById(5);
        assertEquals(b.getId(), 5);
    }

    @Test
    public void testFindBlocksByIdBetween() {

        List<Block> blocks = repository.findBlocksByIdBetween(4, 8);
        assertEquals(blocks.size(), 3);
        assertEquals(blocks.get(0).getId(), 5);
        assertEquals(blocks.get(2).getId(), 7);
    }

    @Test
    public void testFindBlockByIdGreaterThanEqual() {

        List<Block> blocks = repository.findBlocksByIdIsGreaterThanEqual(8);
        assertEquals(blocks.size(), 4);
    }

    @Test
    public void testRemoveBlocksByIdGreaterThan() {

        repository.removeBlocksByIdIsGreaterThan(5);

        Block removed = repository.findBlockById(6);
        assertNull(removed);

        List<Block> blocks = repository.findBlocksByIdIsGreaterThanEqual(1);
        assertEquals(blocks.size(), 5);
    }

    @Test
    public void testFindTop() {

        Block head = repository.findTopByOrderByIdDesc();
        assertEquals(head.getId(), 11);
    }
}
