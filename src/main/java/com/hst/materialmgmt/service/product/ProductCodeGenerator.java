package com.hst.materialmgmt.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ProductCodeGenerator {
    @Autowired private DatabaseClient databaseClient;

    public Mono<String> nextProductCode() {
        return databaseClient.sql("SELECT nextval('rm_material_schema.product_code_seq')")
                .map((row, meta) -> row.get(0, Long.class)).one()
                .map(n -> String.format("PRD-%06d", n));
    }
}