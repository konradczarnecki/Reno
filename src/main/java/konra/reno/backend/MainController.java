package konra.reno.backend;

import konra.reno.Crypto;
import konra.reno.KeysDto;
import konra.reno.Response;
import konra.reno.blockchain.CoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    CoreService service;

    @Autowired
    Crypto crypto;

    @GetMapping("/blockchain-sync")
    public Response<String> syncBlockchain(
            @RequestParam(name = "debug", required = false, defaultValue = "no") String debug){

//        service.downloadBlockchain();

        StringBuffer sb = debug.equals("yes") ? new StringBuffer() : null;
        boolean result = service.verifyBlockchain(sb);

        Response<String> rsp = new Response<>();
        if(result) rsp.setStatus("success");
        else rsp.setStatus("failure");

        if(sb != null) rsp.setContent(sb.toString());

        return rsp;
    }

    @GetMapping("/blockchain-start")
    public Response startBlockchain(){

        boolean result = service.startBlockchain();
        return result ? Response.success() : Response.failure();
    }

    @GetMapping("/create-account")
    public Response<KeysDto> createAccount(){

        KeysDto keys = service.createAccount();

        Response<KeysDto> rsp = new Response<>();
        if(keys != null) rsp.setStatus("success");
        else rsp.setStatus("failure");
        rsp.setContent(keys);

        return rsp;
    }

    @GetMapping("/login")
    public Response<Double> login(
            @RequestParam(name = "prvkey") String privateKey,
            @RequestParam(name = "pubkey") String publicKey ) {

        Double balance = service.login(publicKey, privateKey);

        Response<Double> rsp = new Response<>();

        if(balance == null){

            rsp.setStatus("failure");
            return rsp;
        }

        rsp.setStatus("success");
        rsp.setContent(balance);
        return rsp;
    }



    @GetMapping("/crypto-hash")
    public Response<String> hashValue(@RequestParam(name = "val") String value){

        String hash = crypto.hashB64(value);

        Response<String> rsp = new Response<>();
        rsp.setStatus("success");
        rsp.setContent(hash);
        return rsp;
    }
}
