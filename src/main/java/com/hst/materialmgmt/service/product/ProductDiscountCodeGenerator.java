package com.hst.materialmgmt.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ProductDiscountCodeGenerator {
    @Autowired private DatabaseClient databaseClient;

    public Mono<String> nextDiscountCode() {
        return databaseClient.sql("SELECT nextval('rm_material_schema.product_discount_code_seq')")
                .map((row, meta) -> row.get(0, Long.class)).one()
                .map(n -> String.format("DIS-%06d", n));
    }
}