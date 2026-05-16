package com.hst.materialmgmt.service.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class DepartmentCodeGenerator {

    private static final String NEXTVAL_SQL =
            "SELECT nextval('rm_material_schema.department_code_seq')";
    private static final String CODE_FORMAT = "DEPT-%06d";

    @Autowired
    private DatabaseClient databaseClient;

    public Mono<String> nextDepartmentCode() {
        return databaseClient.sql(NEXTVAL_SQL)
                .map((row, meta) -> row.get(0, Long.class))
                .one()
                .map(n -> String.format(CODE_FORMAT, n));
    }
}
