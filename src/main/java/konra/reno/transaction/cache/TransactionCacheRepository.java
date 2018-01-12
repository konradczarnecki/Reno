package konra.reno.transaction.cache;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionCacheRepository extends MongoRepository<TransactionCache, String> {

    TransactionCache findByAddressIs(String address);
}
