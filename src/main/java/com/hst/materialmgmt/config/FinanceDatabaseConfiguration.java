package com.hst.materialmgmt.config;

import io.r2dbc.spi.ConnectionFactory;
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

    // ConnectionFactory and DatabaseClient are intentionally NOT defined here.
    // Spring Boot auto-configures them from spring.r2dbc.url (DATABASE_URL env var)
    // which already has the correct Neon connection string with SSL.

    @Bean
    @Primary
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