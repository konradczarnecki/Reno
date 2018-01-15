package konra.reno.core.persistance;

import konra.reno.account.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StateRepository extends MongoRepository<Account, String> {

    Account findAccountByAddress(String address);
    List<Account> findAll();
}
