package com.hst.materialmgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.hst.materialmgmt.config.DatabaseProperties;

@SpringBootApplication
@EnableScheduling
@EnableR2dbcRepositories
@EnableConfigurationProperties(DatabaseProperties.class)
public class MaterialMgmtApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MaterialMgmtApplication.class);
        app.setWebApplicationType(WebApplicationType.REACTIVE);
        app.run(args);
    }
}
