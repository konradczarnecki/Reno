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

    @GetMapping("/mine")
    public Response mine(@RequestParam("miner") String miner, @RequestParam("message") String message){

        service.startMining(miner, message);

        return Response.success();
    }

    @GetMapping("/stop-mining")
    public Response stopMine(){

        service.stopMining();

        return Response.success();
    }
}
