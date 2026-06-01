package com.hst.materialmgmt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * Produces the next material_id by finding the lowest unused number.
 * Format: MAT-NNNNNN (zero-padded to six digits).
 * 
 * Uses gap-filling: if MAT-000001 is deleted, the next insert gets MAT-000001.
 * If all rows are deleted, restarts from MAT-000001.
 */
@Component
public class MaterialCodeGenerator {

    private static final String CODE_FORMAT = "MAT-%06d";

    // Find the lowest positive integer NOT already used as a material ID.
    // Works by generating a series 1..N+1 and finding the first gap.
    private static final String NEXT_ID_SQL = """
        SELECT s.n
        FROM generate_series(1, (
            SELECT COUNT(*) + 1
            FROM rm_material_schema.rm_material_tbl
        )) AS s(n)
        WHERE NOT EXISTS (
            SELECT 1 FROM rm_material_schema.rm_material_tbl
            WHERE material_id = CONCAT('MAT-', LPAD(s.n::text, 6, '0'))
        )
        ORDER BY s.n
        LIMIT 1
        """;

    @Autowired
    private DatabaseClient databaseClient;

    public Mono<String> nextMaterialCode() {
        return databaseClient.sql(NEXT_ID_SQL)
                .map((row, meta) -> row.get(0, Long.class))
                .one()
                .map(n -> String.format(CODE_FORMAT, n));
    }
}