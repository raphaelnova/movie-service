package info.raphaelnova.movieservice.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;

import info.raphaelnova.movieservice.entity.MovieEntity;
import info.raphaelnova.movieservice.generated.proto.MovieRequest;
import info.raphaelnova.movieservice.mapper.MovieMapper;
import info.raphaelnova.movieservice.repository.MovieRepository;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class ReactorMovieGraphGrpcServiceTests {

    @Mock
    private MovieRepository repository;

    @Spy
    private MovieMapper mapper = Mappers.getMapper(MovieMapper.class);

    @InjectMocks
    private ReactorMovieGraphGrpcService service;

    MovieEntity movieEntity = new MovieEntity(1L, "Inception", "A mind-bending thriller", 2010, null, null);

    MovieRequest request = MovieRequest.newBuilder()
            .setTitle("Inception")
            .build();

    @Test
    @DisplayName("Should return movie by title")
    void testGetMovieByTitle() {
        when(repository.findOneByTitle(anyString()))
            .thenReturn(Mono.just(movieEntity));

        StepVerifier.create(service.getMovieByTitle(request))
            .expectNextMatches(reply -> "Inception".equals(reply.getTitle()))
            .verifyComplete();
    }

    @Test
    @DisplayName("Should return NOT_FOUND when movie not found")
    void testGetMovieByTitleNotFound() {
        queryResultShouldReturnStatusCode(
            Mono.empty(),
            Status.Code.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return DEADLINE_EXCEEDED on timeout")
    void testGetMovieByTitleTimeout() {
        queryResultShouldReturnStatusCode(
            Mono.delay(Duration.ofSeconds(30)).thenReturn(movieEntity),
            Status.Code.DEADLINE_EXCEEDED);
    }

    @Test
    @DisplayName("Should return UNKNOWN on unexpected error")
    void testGetMovieByTitleUnknownError() {
        queryResultShouldReturnStatusCode(
            Mono.error(new DataRetrievalFailureException("Database exception")),
            Status.Code.UNKNOWN);
    }

    void queryResultShouldReturnStatusCode(Mono<MovieEntity> result, Status.Code expectedCode) {
        when(repository.findOneByTitle(anyString()))
            .thenReturn(result);

        StepVerifier.create(service.getMovieByTitle(request))
            .expectErrorMatches(throwable -> switch(throwable) {
                case StatusRuntimeException ex ->
                    ex.getStatus().getCode() == expectedCode;
                default -> false;
            })
            .verify();
    }
}
