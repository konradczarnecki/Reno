package konra.reno.core.persistance;

import konra.reno.core.block.Block;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlockRepository extends MongoRepository<Block, Long> {

    Block findBlockById(long id);
    List<Block> findBlocksByIdBetween(long fromId, long toId);
    List<Block> findBlocksByIdIsGreaterThanEqual(long fromId);
    void removeBlocksByIdIsGreaterThan(long fromId);
    Block findTopByOrderByIdDesc();
}
