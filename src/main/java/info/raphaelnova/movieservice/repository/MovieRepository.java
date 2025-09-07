package info.raphaelnova.movieservice.repository;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import info.raphaelnova.movieservice.entity.MovieEntity;
import info.raphaelnova.movieservice.annotation.RestoreContextSnapshot;
import reactor.core.publisher.Mono;

@RestoreContextSnapshot
public interface MovieRepository extends ReactiveNeo4jRepository<MovieEntity, Long> {

    /**
     * Finds a movie by its title.
     *
     * @param title the title of the movie
     * @return a Mono containing the movie, or empty if not found
     */
    Mono<MovieEntity> findOneByTitle(String title);
}