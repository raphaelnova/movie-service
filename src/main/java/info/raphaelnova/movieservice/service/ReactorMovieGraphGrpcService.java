package info.raphaelnova.movieservice.service;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.slf4j.MDC;

import info.raphaelnova.movieservice.generated.proto.MovieReply;
import info.raphaelnova.movieservice.generated.proto.MovieRequest;
import info.raphaelnova.movieservice.generated.proto.ReactorMovieGraphGrpc;
import info.raphaelnova.movieservice.mapper.MovieMapper;
import info.raphaelnova.movieservice.repository.MovieRepository;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
// @Service
@RequiredArgsConstructor
public class ReactorMovieGraphGrpcService extends ReactorMovieGraphGrpc.MovieGraphImplBase {

    // Too much trouble using this, Salesforce abandoned it and it's hard to
    // pass context from gRPC to Reactor. It's better to stick with vanilla

    private final MovieRepository movieRepository;
    private final MovieMapper mapper;

    @Override
    public Mono<MovieReply> getMovieByTitle(Mono<MovieRequest> request) {
        MDC.put("service", "ReactorMovieGraphGrpcService");
        log.info("Received request to get movie by title");

        /* ContextRegistry.getInstance()
            .getThreadLocalAccessors()
            .stream()
            .forEach(accessor ->
                log.info("ThreadLocalAccessor: {} = {}", accessor.key(), accessor.getValue())); */

        return request.map(MovieRequest::getTitle)
                .flatMap(title -> {
                    log.info("Inside flatMap");
                    /* ContextRegistry.getInstance()
                        .getThreadLocalAccessors()
                        .stream()
                        .forEach(accessor ->
                            log.info("flatMap ThreadLocalAccessor: {} = {}", accessor.key(), accessor.getValue())); */
                    return movieRepository.findOneByTitle(title)
                        .timeout(Duration.ofSeconds(5))
                        .flatMap(movie -> {
                            return Mono.deferContextual(ctx -> {

                                /* ContextRegistry.getInstance()
                                    .getThreadLocalAccessors()
                                    .stream()
                                    .forEach(accessor ->
                                        log.info("defer ThreadLocalAccessor: {} = {}", accessor.key(), accessor.getValue()));
                                ctx.stream().forEach(entry ->
                                    log.info("Context entry: {} = {}", entry.getKey(), entry.getValue())); */
                                return Mono.just(movie);
                            });
                        })
                        .map(mapper::toMovieReply)
                        .doOnNext(movieReply -> log.info("Found movie '{}' with id {}", title, movieReply.getId()))
                        .switchIfEmpty(Mono.error(
                            Status.NOT_FOUND
                                .withDescription("No movie found for title " + title)
                                .asRuntimeException()))
                        .contextCapture();
                })
                .doOnError(throwable -> log.error("Error retrieving movie by title.", throwable))
                .onErrorMap(throwable -> switch (throwable) {
                    case StatusRuntimeException ex -> ex;
                    case TimeoutException ex -> Status.DEADLINE_EXCEEDED
                        .withDescription("Database timeout")
                        .asRuntimeException();
                    default -> Status.UNKNOWN
                        .withDescription("Error finding movie")
                        .withCause(throwable)
                        .asRuntimeException();
                })
                .contextWrite(ctx -> ctx.put("ctx-key", "Mono<MovieRequest>"))
                .contextCapture();
    }
}