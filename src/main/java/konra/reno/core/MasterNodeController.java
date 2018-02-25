package konra.reno.core;

import konra.reno.util.Response;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("master")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MasterNodeController {

    CoreService core;

    @Autowired
    public MasterNodeController(CoreService core) {
        this.core = core;
    }

    @GetMapping("/init")
    public Response startBlockchain() {

        core.startBlockchain();
        return Response.success();
    }
}
