package konra.reno.transaction;

import konra.reno.util.Response;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionController {

    TransactionService service;

    @Autowired
    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping("/new-transaction")
    public Response<Transaction> newTransaction(@RequestBody Transaction transaction, @RequestParam("key") String key) {

        Transaction created = service.newTx(transaction, key);

        Response<Transaction> rsp = new Response<>();
        rsp.setStatus("success");
        rsp.setContent(created);
        return rsp;
    }

    @GetMapping("/transaction-status")
    public Response<TransactionStatus> checkTransactionStatus(@RequestParam("hash") String hash) {

        TransactionStatus txStatus = service.checkTxStatus(hash);

        Response<TransactionStatus> rsp = new Response<>();
        rsp.setStatus("success");
        rsp.setContent(txStatus);
        return rsp;
    }

    @GetMapping("/get-transactions")
    public Response<List<Transaction>> getTransactionsByAddress(@RequestParam("address") String address) {

        List<Transaction> transactions = service.getTxsByAddress(address);

        Response<List<Transaction>> rsp = new Response<>();
        rsp.setStatus("success");
        rsp.setContent(transactions);
        return rsp;
    }
}
