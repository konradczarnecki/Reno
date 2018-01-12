package konra.reno.account;

import konra.reno.util.KeysDto;
import konra.reno.util.Response;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
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
    public Response login(String publicKey, String privateKey) {

        boolean result = service.login(publicKey, privateKey);
        return result ? Response.success() : Response.failure();
    }
}
