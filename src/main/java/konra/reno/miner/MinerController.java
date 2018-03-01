package konra.reno.miner;

import konra.reno.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MinerController {

    MinerService service;

    @Autowired
    public MinerController(MinerService service) {
        this.service = service;
    }

    @GetMapping("/miner-start")
    public Response mine(@RequestParam("miner") String miner,
                         @RequestParam(value = "message", required = false, defaultValue = "") String message) {

        service.startMining(miner, message);

        return Response.success();
    }

    @GetMapping("/miner-stop")
    public Response stopMine() {

        service.stopMining();

        return Response.success();
    }

    @GetMapping("/miner-status")
    public Response<MinerStatus> minerStatus() {

        MinerStatus status = service.checkStatus();

        Response<MinerStatus> rsp = new Response<>();
        rsp.setStatus("success");
        rsp.setContent(status);
        return rsp;
    }
}
