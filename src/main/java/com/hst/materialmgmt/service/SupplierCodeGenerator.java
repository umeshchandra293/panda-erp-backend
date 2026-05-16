package com.hst.materialmgmt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * Produces the next supplier_code from the {@code supplier_code_seq} sequence.
 * <p>
 * Format: {@code SUPP-NNNNNN} (zero-padded to six digits). The sequence lives in the
 * {@code erp_finance_schema} schema and is created by the supplier-mgmt-tables.sql
 * migration. The leading 6-digit width is cosmetic — the sequence overflows to 7 digits
 * cleanly once we hit a million suppliers, which we will not.
 */
@Component
public class SupplierCodeGenerator {

    private static final String NEXTVAL_SQL =
            "SELECT nextval('erp_finance_schema.supplier_code_seq')";

    private static final String CODE_FORMAT = "SUPP-%06d";

    @Autowired
    private DatabaseClient databaseClient;

    public Mono<String> nextSupplierCode() {
        return databaseClient.sql(NEXTVAL_SQL)
                .map((row, meta) -> row.get(0, Long.class))
                .one()
                .map(n -> String.format(CODE_FORMAT, n));
    }
}
