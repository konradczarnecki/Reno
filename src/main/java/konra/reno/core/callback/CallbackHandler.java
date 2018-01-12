package konra.reno.core.callback;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

@Service
public class CallbackHandler {

    Map<CallbackType, Runnable> runnables;
    Map<CallbackType, Consumer> consumers;

    ScheduledExecutorService exec;
}
