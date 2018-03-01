package konra.reno.account;

import konra.reno.util.KeysDto;
import konra.reno.util.Response;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class AccountController {

    AccountService service;

    @Autowired
    public AccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping("/new-account")
    public Response<Account> newAccount() {

        Account account = service.createAccount();

        Response<Account> rsp = new Response<>();
        rsp.setStatus("success");
        rsp.setContent(account);
        return rsp;
    }

    @SneakyThrows
    @GetMapping(value = "/encrypt-keyfile", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public FileSystemResource encryptKeyfile(@RequestParam("publicKey") String publicKey,
                                             @RequestParam("privateKey") String privateKey,
                                             @RequestParam("password") String password) {

        String encrypted = service.encryptKeyfile(publicKey, privateKey, password);
        File temp = File.createTempFile("keystore", ".ks");
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(encrypted);
        bw.close();

        return new FileSystemResource(temp);
    }

    @GetMapping("/login-keyfile")
    public Response<Account> loginWithKeyfile(@RequestParam("keyfileContent") String keyfile,
                                              @RequestParam("password") String password) {

        Account account = service.login(keyfile, password);

        Response<Account> rsp = new Response<>();
        rsp.setStatus("success");
        rsp.setContent(account);
        return rsp;
    }

    @GetMapping("/account-status")
    public Response<Account> accountStatus(@RequestParam("address") String address) {

        Account account = service.getAccountByAddress(address);

        Response<Account> rsp = new Response<>();
        rsp.setStatus("status");
        rsp.setContent(account);
        return rsp;
    }
}
