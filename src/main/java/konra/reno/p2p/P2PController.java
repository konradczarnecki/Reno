package konra.reno.p2p;

import konra.reno.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;

@RestController
@RequestMapping("/p2p")
public class P2PController {

    P2PService service;

    @Autowired
    public P2PController(P2PService service) {

        this.service = service;
    }

    @GetMapping("/bootstrap")
    public Response bootstrap(
            @RequestParam(name = "host", required = false, defaultValue = "initial") String host)
            throws UnknownHostException {

        P2PService.setHost(host);
        return service.bootstrap() ? Response.success() : Response.failure();
    }

    @GetMapping("/set-host")
    public Response setHost(@RequestParam(name = "host") String host){

        P2PService.setHost(host);
        return Response.success();
    }

    @GetMapping("/login-com")
    public Response loginCommon(){

        return service.loginCommon() ? Response.success() : Response.failure();
    }

    @GetMapping("/logout")
    public Response logout(){

        return service.logout() ? Response.success() : Response.failure();
    }

    @GetMapping("/add-file")
    public Response addFile(@RequestParam(name = "name") String name,
                            @RequestParam(name = "content") String content){

        return service.addTextFile(name, content) ? Response.success() : Response.failure();
    }

    @GetMapping("/download-file")
    public Response downloadFile(@RequestParam(name = "name") String name){

        service.downloadFile(name);
        return  Response.success();
    }
}
