package konra.reno.miner;

import konra.reno.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MinerController {

    MinerService service;

    @Autowired
    public MinerController(MinerService service) {
        this.service = service;
    }

    @GetMapping("/mine")
    public Response mine(){

        return Response.success();
    }

    @GetMapping("/stop-mining")
    public Response stopMine(){


        return Response.success();
    }
}
