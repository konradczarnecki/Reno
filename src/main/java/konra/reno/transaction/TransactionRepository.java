package konra.reno.transaction;

import konra.reno.core.block.Block;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;

import static org.springframework.data.mongodb.core.query.Criteria.*;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
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

        Predicate<Transaction> isRelevant = transaction ->
                transaction.getSender().equals(address) || transaction.getReceiver().equals(address);

        Query query = new Query().addCriteria(cr);

        return template.find(query, Block.class, "blockchain").stream()
                .map(Block::getTransactions)
                .flatMap(Collection::stream)
                .filter(isRelevant)
                .collect(Collectors.toList());
    }

    public Transaction getTxByHash(String hash) {

        Criteria cr = where("transactions.hash").is(hash);
        Query query = new Query().addCriteria(cr);

        return template.findOne(query, Block.class, "blockchain")
                .getTransactions().stream()
                .filter(transaction -> transaction.getHash().equals(hash))
                .findFirst().get();
    }
}
