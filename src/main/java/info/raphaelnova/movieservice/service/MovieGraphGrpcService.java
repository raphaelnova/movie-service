package info.raphaelnova.movieservice.service;

import org.springframework.stereotype.Service;

import info.raphaelnova.movieservice.generated.proto.MovieGraphGrpc;
import info.raphaelnova.movieservice.generated.proto.MovieReply;
import info.raphaelnova.movieservice.generated.proto.MovieRequest;
import info.raphaelnova.movieservice.mapper.MovieMapper;
import info.raphaelnova.movieservice.repository.MovieRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieGraphGrpcService extends MovieGraphGrpc.MovieGraphImplBase {

    private final MovieRepository movieRepository;

    private final MovieMapper mapper;

    @Override
    public void getMovieByTitle(MovieRequest request, StreamObserver<MovieReply> responseObserver) {
        String movieTitle = request.getTitle();

        log.info("Starting getMovieByTitle for title '{}'", movieTitle);

        movieRepository.findOneByTitle(movieTitle)
            .doOnNext(movie -> {
                log.info("Found movie id: {}", movie.getId());
                log.debug("Movie details: {}", movie);

                responseObserver.onNext(mapper.toMovieReply(movie));
                responseObserver.onCompleted();
                log.info("Completed getMovieByTitle for title '{}'", movieTitle);
            })
            .switchIfEmpty(Mono.fromRunnable(() -> {
                String msg = String.format("No movie found for title '%s'", movieTitle);
                log.warn(msg);
                responseObserver.onError(Status.NOT_FOUND
                    .withDescription(msg)
                    .asRuntimeException());
            }))
            .doOnError(error -> {
                String msg = String.format("An error occurred while retrieving the movie '%s'", movieTitle);
                log.error(msg, error);
                responseObserver.onError(Status.UNKNOWN
                    .withDescription(msg)
                    .withCause(error)
                    .asRuntimeException());
            })
            .subscribe();

        log.info("Assembled getMovieByTitle Flux pipeline.");
    }
}
