package konra.reno.blockchain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlockRepository extends MongoRepository<Block, Long> {

    Block getBlockById(long id);
}
