package konra.reno.p2p;

import konra.reno.util.Response;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class P2PController {

    P2PService service;

    @Autowired
    public P2PController(P2PService service) {
        this.service = service;
    }

    @GetMapping("/p2p-connect")
    public Response connect() {

        service.connect();
        return Response.success();
    }

    @GetMapping("/p2p-status")
    public Response<P2PStatus> checkConnect() {

        P2PStatus status = service.checkStatus();

        Response<P2PStatus> rsp = new Response<>();
        rsp.setStatus("success");
        rsp.setContent(status);
        return rsp;
    }
}
