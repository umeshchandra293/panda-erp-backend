package com.hst.materialmgmt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "erp.finance.api.db")
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
}