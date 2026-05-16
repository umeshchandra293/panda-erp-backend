package com.hst.materialmgmt.service.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * Produces the next company_id from the {@code company_code_seq} sequence.
 * Format: {@code COM-NNNNNN} (zero-padded to six digits).
 */
@Component
public class CompanyCodeGenerator {

    private static final String NEXTVAL_SQL =
            "SELECT nextval('rm_material_schema.company_code_seq')";

    private static final String CODE_FORMAT = "COM-%06d";

    @Autowired
    private DatabaseClient databaseClient;

    public Mono<String> nextCompanyCode() {
        return databaseClient.sql(NEXTVAL_SQL)
                .map((row, meta) -> row.get(0, Long.class))
                .one()
                .map(n -> String.format(CODE_FORMAT, n));
    }
}
