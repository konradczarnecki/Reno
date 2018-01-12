package konra.reno.core.callback;

import org.springframework.stereotype.Service;

import javax.security.auth.callback.Callback;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

@Service
public class CallbackHandler {

    Map<CallbackType, Runnable> runnables;
    Map<CallbackType, Consumer> consumers;

    ScheduledExecutorService exec;

    public CallbackHandler() {

        runnables = new HashMap<>();
        consumers = new HashMap<>();
        exec = Executors.newScheduledThreadPool(10);
    }

    public void register(CallbackType type, Runnable callback) {

        runnables.put(type, callback);
    }

    public void register(CallbackType type, Consumer callback) {

        consumers.put(type, callback);
    }

    public void execute(CallbackType type) {

        if(runnables.containsKey(type)) exec.execute(runnables.get(type));
        else throw new RuntimeException("No callbacks of type " + type + " registered");
    }

    @SuppressWarnings("unchecked")
    public void execute(CallbackType type, Object payload) {

        if(!consumers.containsKey(type)) return;
        Runnable callback = () -> consumers.get(type).accept(payload);
        exec.execute(callback);
    }
}
