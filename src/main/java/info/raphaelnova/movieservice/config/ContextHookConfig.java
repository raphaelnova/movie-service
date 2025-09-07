package info.raphaelnova.movieservice.config;

import io.micrometer.context.ContextRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

@Configuration
public class ContextHookConfig {
    @PostConstruct
    void setupHooks() {
        ContextRegistry.getInstance()
                .registerThreadLocalAccessor("mdc-context", MDC::getCopyOfContextMap, MDC::setContextMap, MDC::clear)
                .loadThreadLocalAccessors();

        Hooks.enableAutomaticContextPropagation();
    }
}
