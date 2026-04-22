package com.hst.materialmgmt.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "erp.finance.api.db")
@Component
@Data
public class DatabaseProperties {
  private String hostName;
  private int port;
  private String databaseName;
  private String schemaName;
  private String username;
  private String password;
  private int connectionpoolInitialSize;
  private int connectionpoolMaxSize;
  private int connectionpoolMaxWaitTime;
  private int connectionpoolMaxIdleTime;
  private int connectionpoolMaxLifetime;

  @Bean
  @ConditionalOnMissingBean(DatabaseProperties.class)
  public DatabaseProperties databaseProperties() {
    return new DatabaseProperties();
  }
}
