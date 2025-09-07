package info.raphaelnova.movieservice.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Hooks;

@Component
@Profile("dev")
public class ReactorDebugConfig {

    @PostConstruct
    public void enableOperatorDebug() {
        Hooks.onOperatorDebug();
    }
}
