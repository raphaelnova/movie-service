package info.raphaelnova.movieservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import info.raphaelnova.movieservice.config.ReactiveNeo4jBeanConfiguration;
import info.raphaelnova.movieservice.entity.MovieEntity;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@DataNeo4jTest
@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
@Import(ReactiveNeo4jBeanConfiguration.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class MovieRepositoryTest {

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    TransactionalOperator tx;

    @Container
    @ServiceConnection
    @SuppressWarnings("resource")
    static Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:latest")
        .withoutAuthentication();

    @BeforeAll
    void initData() {
        log.info("Initializing Neo4j database with test data...");
        String boltUrl = neo4jContainer.getBoltUrl();
        try (Driver driver = GraphDatabase.driver(boltUrl,AuthTokens.none());
             Session session = driver.session()) {

            String cypher = Files.readString(Path.of("src/test/resources/MovieRepositoryTests.cypher"));
            for (String stmt : cypher.split(";")) {
                if (!stmt.isBlank()) {
                    session.run(stmt);
                }
            }
        } catch(IOException e) {
            throw new RuntimeException("Failed to read initialization script", e);
        }
    }

    @Test
    void testFindByTitle() {
        Mono<MovieEntity> result = movieRepository
            .findOneByTitle("What Dreams May Come")
            .as(tx::transactional);

        StepVerifier.create(result)
            .assertNext(movie -> {
                log.info("Found movie: {}", movie);
                assertEquals("What Dreams May Come", movie.getTitle());
                assertEquals(1998, movie.getReleased());
                assertEquals(5, movie.getActorsAndRoles().size());

                var robin = movie.getActorsAndRoles().stream()
                    .filter(role -> role.getActor().getName().equals("Robin Williams"))
                    .findFirst()
                    .orElseThrow();

                assertEquals(1951, robin.getActor().getBorn());
                assertTrue(robin.getRoles().contains("Chris Nielsen"));

                var vincent = movie.getDirectors().stream()
                    .filter(d -> d.getName().equals("Vincent Ward"))
                    .findFirst()
                    .orElseThrow();

                assertEquals(1956, vincent.getBorn());
            })
            .verifyComplete();
    }
}

