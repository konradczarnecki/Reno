package konra.reno.p2p;

import konra.reno.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class P2PController {

    private P2PService service;

    @Autowired
    public P2PController(P2PService service) {
        this.service = service;
    }

    @GetMapping("/connect")
    public Response<Long> connect() {

        service.connect();
        return Response.success();
    }
}
