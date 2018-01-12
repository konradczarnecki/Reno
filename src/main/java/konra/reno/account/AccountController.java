package konra.reno.account;

import konra.reno.util.KeysDto;
import konra.reno.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    AccountService service;

    @Autowired
    public AccountController(AccountService service) {
        this.service = service;
    }
//
//    @GetMapping("/new-account")
//    public Response<KeysDto> newAccount() {
//
//
//    }
}
