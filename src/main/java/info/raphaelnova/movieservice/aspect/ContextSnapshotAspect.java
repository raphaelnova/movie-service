package info.raphaelnova.movieservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import io.micrometer.context.ContextSnapshot;
import io.micrometer.context.ContextSnapshotFactory;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Aspect
@Component
public class ContextSnapshotAspect {

    private final ContextSnapshotFactory contextSnapshotFactory = ContextSnapshotFactory.builder().build();

    @Around("@annotation(RestoreContextSnapshot) || @within(RestoreContextSnapshot)")
    public Object wrapWithContext(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        log.info("Inside ContextSnapshotAspect");
        if (result instanceof Mono<?> mono) {
            ContextSnapshot snapshot = contextSnapshotFactory.captureAll();
            return Mono.defer(() -> {
                try (ContextSnapshot.Scope scope = snapshot.setThreadLocals()) {
                    log.info("Deferring context for Mono");
                    return mono;
                }
            });
        } else if (result instanceof Flux<?> flux) {
            ContextSnapshot snapshot = contextSnapshotFactory.captureAll();
            return Flux.defer(() -> {
                try (ContextSnapshot.Scope scope = snapshot.setThreadLocals()) {
                    log.info("Deferring context for Flux");
                    return flux;
                }
            });
        }

        return result;
    }
}

