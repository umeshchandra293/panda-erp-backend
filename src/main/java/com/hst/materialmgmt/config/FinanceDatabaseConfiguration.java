package com.hst.materialmgmt.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
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

import java.time.Duration;

@Configuration
public class FinanceDatabaseConfiguration extends DatabaseConfiguration {

    @Value("${erp.finance.api.db.hostName}")    private String  host;
    @Value("${erp.finance.api.db.port}")         private int     port;
    @Value("${erp.finance.api.db.databaseName}") private String  database;
    @Value("${erp.finance.api.db.schemaName}")   private String  schema;
    @Value("${erp.finance.api.db.username}")      private String  username;
    @Value("${erp.finance.api.db.password}")      private String  password;

    @Bean
    @Primary
    @ConditionalOnMissingBean(ConnectionFactory.class)
    public ConnectionFactory connectionFactory() {
        System.out.println("=== DB HOST: " + host + " PORT: " + port + " DB: " + database + " ===");

        PostgresqlConnectionConfiguration.Builder builder =
            PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .database(database)
                .schema(schema)
                .username(username)
                .password(password);

        // Enable SSL for any non-localhost host (Neon, RDS, etc.)
        if (host != null && !host.startsWith("localhost") && !host.equals("127.0.0.1")) {
            builder.enableSsl();
        }

        ConnectionFactory cf = new PostgresqlConnectionFactory(builder.build());

        return new ConnectionPool(
            ConnectionPoolConfiguration.builder(cf)
                .initialSize(1)
                .maxSize(3)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofMinutes(3))
                .validationQuery("SELECT 1")
                .build()
        );
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
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager tm) {
        return super.getTransactionalOperator(tm);
    }

    @Bean
    @ConditionalOnMissingBean(ReactiveTransactionManager.class)
    public ReactiveTransactionManager reactiveTransactionManager(ConnectionFactory cf) {
        return super.getTransactionManager(cf);
    }

    @Bean
    @ConditionalOnMissingBean(ConnectionFactoryInitializer.class)
    public ConnectionFactoryInitializer connectionFactoryInitializer(ConnectionFactory cf) {
        return super.getConnectionFactoryInitializer(cf);
    }
}