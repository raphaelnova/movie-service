package info.raphaelnova.movieservice.config;

import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.neo4j.driver.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.ReactiveDatabaseSelectionProvider;
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
public class ReactiveNeo4jBeanConfiguration {

    /**
     * Provides a Cypher DSL configuration for Neo4J. This configuration
     * sets the dialect to Neo4J 5, which is necessary for using the
     * Cypher DSL in a way that is compatible with Neo4j 5 features.
     *
     * @return a Configuration instance for Cypher DSL
     */
    @Bean
    org.neo4j.cypherdsl.core.renderer.Configuration cypherDslConfiguration() {
        return org.neo4j.cypherdsl.core.renderer.Configuration
                .newConfig().withDialect(Dialect.NEO4J_5).build();
    }

    /**
     * Provides a reactive transaction manager for Neo4j. This bean connects 
     * Spring’s transaction infrastructure to the Neo4j reactive driver.
     * It is required for any transactional behavior (explicit or implicit)
     * in reactive repositories or templates, and also to back the
     * <code>TransactionalOperator</code>.
     *
     * @param driver the Neo4j driver
     * @param databaseNameProvider the provider for the database name
     * @return a ReactiveTransactionManager instance
     */
    @Bean
    ReactiveTransactionManager reactiveTransactionManager(
            Driver driver,
            ReactiveDatabaseSelectionProvider databaseNameProvider) {
        return new ReactiveNeo4jTransactionManager(driver, databaseNameProvider);
    }

    /**
     * Provides a <code>TransactionalOperator</code> for applying
     * transactions boundaries in a reactive context. Any attempt
     * to use a reactive repository or template without this operator
     * will result in a NullPointerException.
     *
     * @param transactionManager the reactive transaction manager
     * @return a TransactionalOperator instance
     */
    @Bean
    TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }
}
