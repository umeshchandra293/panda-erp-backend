package com.hst.materialmgmt.config;

import java.time.Duration;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;

public class DatabaseConfiguration {
  public ConnectionFactory connectionFactory(DatabaseProperties properties) {
    PostgresqlConnectionConfiguration config =
        PostgresqlConnectionConfiguration.builder()
            .host(properties.getHostName())
            .port(properties.getPort())
            .username(properties.getUsername())
            .password(properties.getPassword())
            .database(properties.getDatabaseName())
            .schema(properties.getSchemaName())
            // .sslMode(SSLMode.DISABLE) // Adjust SSL mode as needed)
            .build();

    ConnectionFactory connectionFactory = new PostgresqlConnectionFactory(config);

    ConnectionPoolConfiguration poolConfig =
        ConnectionPoolConfiguration.builder(connectionFactory)
            .initialSize(properties.getConnectionpoolInitialSize())
            .maxSize(properties.getConnectionpoolMaxSize())
            .maxIdleTime(Duration.ofMinutes(properties.getConnectionpoolMaxIdleTime()))
            .maxLifeTime(Duration.ofMinutes(properties.getConnectionpoolMaxWaitTime()))
            .validationQuery("SELECT 1")
            .build();
    return new ConnectionPool(poolConfig);
  }

  /*
  public static TransactionAwareConnectionFactoryProxy getTransactionAwareConnectionFactoryProxy(ConnectionFactory connectionFactory)  {
      return new TransactionAwareConnectionFactoryProxy(connectionFactory);
  }
  */

  public static DatabaseClient getDatabaseClient(ConnectionFactory connectionFactory) {
    return DatabaseClient.builder()
        .connectionFactory(connectionFactory)
        .namedParameters(true)
        .build();
  }

  public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
    return new R2dbcEntityTemplate(connectionFactory);
  }

  /*
  public static R2dbcEntityTemplate getR2dbcEntityTemplate(ConnectionFactory connectionFactory) {
      return new R2dbcEntityTemplate(getTransactionAwareConnectionFactoryProxy(connectionFactory));
  }
  */

  public static TransactionalOperator getTransactionalOperator(
      ReactiveTransactionManager transactionManager) {
    DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
    definition.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_READ_COMMITTED);
    definition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
    definition.setTimeout(300); // Set timeout as needed
    return TransactionalOperator.create(transactionManager, definition);
  }

  public static ReactiveTransactionManager getTransactionManager(
      ConnectionFactory connectionFactory) {
    return new R2dbcTransactionManager(connectionFactory);
  }

  public static ConnectionFactoryInitializer getConnectionFactoryInitializer(
      ConnectionFactory connectionFactory) {
    ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
    initializer.setConnectionFactory(connectionFactory);
    // Add any database initialization scripts or settings here if needed
    // For example, you can set a script to run on startup
    // initializer.setDatabasePopulator(new ResourceDatabasePopulator(new
    // ClassPathResource("schema.sql")));
    return initializer;
  }
}
