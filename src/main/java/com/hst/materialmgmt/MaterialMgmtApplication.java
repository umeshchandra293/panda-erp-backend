package com.hst.materialmgmt;

import javax.annotation.processing.Generated;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Generated(value = "com.hst.materialmgmt.MaterialMgmtApplication", date = "2026-04-10T12:00:00Z")
@EnableR2dbcRepositories
public class MaterialMgmtApplication {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(MaterialMgmtApplication.class);
    app.setWebApplicationType(WebApplicationType.REACTIVE);
    app.run(args);
  }
}
