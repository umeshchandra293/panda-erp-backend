package com.hst.materialmgmt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * Server-generated movement codes: MOV-NNNNNN.
 */
@Component
public class MovementCodeGenerator {

    private static final String NEXTVAL_SQL =
            "SELECT nextval('erp_finance_schema.movement_code_seq')";
    private static final String FORMAT = "MOV-%06d";

    @Autowired
    private DatabaseClient databaseClient;

    public Mono<String> nextMovementCode() {
        return databaseClient.sql(NEXTVAL_SQL)
                .map((row, meta) -> row.get(0, Long.class))
                .one()
                .map(n -> String.format(FORMAT, n));
    }
}
