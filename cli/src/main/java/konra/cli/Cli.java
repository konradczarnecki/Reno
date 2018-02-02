package konra.cli;

import konra.reno.util.KeysDto;
import konra.reno.util.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.client.RestTemplate;


@ShellComponent
public class Cli {

    @Value("${port}")
    int port;

    RestTemplate rest;

    public Cli() {

        rest = new RestTemplate();
    }

    @ShellMethod(value = "Create new account.", key = "new-account")
    public String createAccount() {

        Response<KeysDto> rsp = (Response<KeysDto>) rest.getForObject("http://localhost:" + port + "/new-account", Response.class);
        KeysDto keys =  rsp.getContent();

        return "Address: " + keys.getPublicKey() + "\n" + "Private key: " + keys.getPrivateKey();
    }
}
