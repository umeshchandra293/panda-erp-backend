package com.hst.materialmgmt.config;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import java.time.Duration;

@Configuration
public class FinanceDatabaseConfiguration extends DatabaseConfiguration {

    @Autowired
    private DatabaseProperties databaseProperties;

    @Bean
    @Primary
    @ConditionalOnMissingBean(ConnectionFactory.class)
    public ConnectionFactory connectionFactory() {
        PostgresqlConnectionConfiguration.Builder builder =
            PostgresqlConnectionConfiguration.builder()
                .host(databaseProperties.getHostName())
                .port(databaseProperties.getPort())
                .username(databaseProperties.getUsername())
                .password(databaseProperties.getPassword())
                .database(databaseProperties.getDatabaseName())
                .schema(databaseProperties.getSchemaName());

        // Enable SSL for cloud databases (Neon, RDS, etc.)
        String host = databaseProperties.getHostName();
        if (host != null && !host.equals("localhost") && !host.equals("127.0.0.1")) {
            builder.enableSsl();
        }

        ConnectionFactory connectionFactory = new PostgresqlConnectionFactory(builder.build());

        ConnectionPoolConfiguration poolConfig =
            ConnectionPoolConfiguration.builder(connectionFactory)
                .initialSize(Math.min(databaseProperties.getConnectionpoolInitialSize(), 2))
                .maxSize(Math.min(databaseProperties.getConnectionpoolMaxSize(), 5))
                .maxIdleTime(Duration.ofSeconds(30))
                .maxLifeTime(Duration.ofMinutes(5))
                .validationQuery("SELECT 1")
                .build();

        return new ConnectionPool(poolConfig);
    }

    @Bean
    @ConditionalOnMissingBean(DatabaseClient.class)
    public DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
        return super.getDatabaseClient(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean(R2dbcEntityTemplate.class)
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return super.r2dbcEntityTemplate(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean(TransactionalOperator.class)
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        return super.getTransactionalOperator(transactionManager);
    }

    @Bean
    @ConditionalOnMissingBean(ReactiveTransactionManager.class)
    public ReactiveTransactionManager reactiveTransactionManager(ConnectionFactory connectionFactory) {
        return super.getTransactionManager(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean(ConnectionFactoryInitializer.class)
    public ConnectionFactoryInitializer connectionFactoryInitializer(ConnectionFactory connectionFactory) {
        return super.getConnectionFactoryInitializer(connectionFactory);
    }
}