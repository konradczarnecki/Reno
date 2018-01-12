package konra.reno.transaction;

import konra.reno.core.Block;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;

import static org.springframework.data.mongodb.core.query.Criteria.*;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionRepository {

    MongoTemplate template;

    public TransactionRepository(MongoTemplate template) {
        this.template = template;
    }

    public List<Transaction> getTxsByAddress(String address, long startingBlock) {

        Criteria cr = new Criteria()
                .orOperator(where("transactions.sender").is(address), where("transactions.receiver").is(address))
                .and("id").gte(startingBlock);

        Query query = new Query();
        query.addCriteria(cr);

        return template.find(query, Block.class, "blockchain").stream()
                .map(Block::getTransactions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public Transaction getTxByHash(String hash) {

    }
}
