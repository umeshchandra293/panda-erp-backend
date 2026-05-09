package com.hst.materialmgmt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * Produces the next material_id from the {@code material_code_seq} sequence.
 * Format: {@code MAT-NNNNNN} (zero-padded to six digits).
 */
@Component
public class MaterialCodeGenerator {

    private static final String NEXTVAL_SQL =
            "SELECT nextval('erp_finance_schema.material_code_seq')";

    private static final String CODE_FORMAT = "MAT-%06d";

    @Autowired
    private DatabaseClient databaseClient;

    public Mono<String> nextMaterialCode() {
        return databaseClient.sql(NEXTVAL_SQL)
                .map((row, meta) -> row.get(0, Long.class))
                .one()
                .map(n -> String.format(CODE_FORMAT, n));
    }
}