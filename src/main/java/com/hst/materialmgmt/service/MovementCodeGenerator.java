package com.hst.materialmgmt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MovementCodeGenerator {

    private static final String FORMAT = "MOV-%06d";

    private static final String NEXT_ID_SQL = """
        SELECT s.n FROM generate_series(1,(SELECT COUNT(*)+1 FROM erp_finance_schema.rm_stock_movement_tbl)) AS s(n)
        WHERE NOT EXISTS (
            SELECT 1 FROM erp_finance_schema.rm_stock_movement_tbl
            WHERE movement_id = CONCAT('MOV-', LPAD(s.n::text,6,'0'))
        ) ORDER BY s.n LIMIT 1
        """;

    @Autowired
    private DatabaseClient databaseClient;

    public Mono<String> nextMovementCode() {
        return databaseClient.sql(NEXT_ID_SQL)
                .map((row, meta) -> row.get(0, Long.class))
                .one()
                .map(n -> String.format(FORMAT, n));
    }
}