package konra.reno.blockchain;

import konra.reno.account.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StateRepository extends MongoRepository<Account, String> {

    Account getAccountByAddress(String address);
    List<Account> getAllByAddressNotNull();
}
