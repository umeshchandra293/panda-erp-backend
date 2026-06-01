package com.hst.materialmgmt.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ProductDiscountCodeGenerator {
    @Autowired private DatabaseClient databaseClient;

    private static final String NEXT_ID_SQL = """
        SELECT s.n FROM generate_series(1,(SELECT COUNT(*)+1 FROM rm_material_schema.product_discount_tbl)) AS s(n)
        WHERE NOT EXISTS (
            SELECT 1 FROM rm_material_schema.product_discount_tbl
            WHERE discount_id = CONCAT('DIS-', LPAD(s.n::text,6,'0'))
        ) ORDER BY s.n LIMIT 1
        """;

    public Mono<String> nextDiscountCode() {
        return databaseClient.sql(NEXT_ID_SQL)
                .map((row, meta) -> row.get(0, Long.class)).one()
                .map(n -> String.format("DIS-%06d", n));
    }
}