package konra.reno.backend;

import konra.reno.p2p.P2PService;
import konra.reno.util.Crypto;
import konra.reno.util.KeysDto;
import konra.reno.util.Response;
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
    P2PService p2PService;

    @Autowired
    Crypto crypto;

    @GetMapping("/blockchain-sync")
    public Response<String> syncBlockchain(
            @RequestParam(name = "debug", required = false, defaultValue = "no") String debug){

//        service.downloadBlockchain();

        StringBuilder sb = debug.equals("yes") ? new StringBuilder() : null;
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


        Response<KeysDto> rsp = new Response<>();

        return rsp;
    }

    @GetMapping("/login")
    public Response<Double> login(
            @RequestParam(name = "prvkey") String privateKey,
            @RequestParam(name = "pubkey") String publicKey ) {

        Double balance = 5d;

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

    @GetMapping("/sync")
    public Response<String> sync(){

        p2PService.runSyncProcess();

        return Response.success();
    }
}
