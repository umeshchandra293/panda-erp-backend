package com.hst.materialmgmt.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
public class FinanceDatabaseConfiguration extends DatabaseConfiguration {

    // Reads directly from DATABASE_URL env variable via spring.r2dbc.url
    @Value("${spring.r2dbc.url}")
    private String r2dbcUrl;

    @Bean
    @Primary
    @ConditionalOnMissingBean(ConnectionFactory.class)
    public ConnectionFactory connectionFactory() {
        System.out.println("=== CONNECTING TO: " + r2dbcUrl + " ===");
        return ConnectionFactories.get(r2dbcUrl);
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