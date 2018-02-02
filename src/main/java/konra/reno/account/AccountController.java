package konra.reno.account;

import konra.reno.util.KeysDto;
import konra.reno.util.Response;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountController {

    AccountService service;

    @Autowired
    public AccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping("/new-account")
    public Response<KeysDto> newAccount() {

        Account account = service.createAccount();

        Response<KeysDto> rsp = new Response<>();
        rsp.setStatus("success");
        rsp.setContent(account.getKeys());
        return rsp;
    }

    @GetMapping("/login")
    public Response<Account> login(String publicKey, String privateKey) {

        Account account = service.login(publicKey, privateKey);

        Response<Account> rsp = new Response<>();
        rsp.setStatus("success");
        rsp.setContent(account);
        return rsp;
    }
}
