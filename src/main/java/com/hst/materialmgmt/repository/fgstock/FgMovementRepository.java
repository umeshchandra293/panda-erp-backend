package com.hst.materialmgmt.repository.fgstock;

import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.entity.fgstock.FgMovementEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.fgstock.FgMovementRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class FgMovementRepository extends ParentRepositoryImpl {

    @Autowired private FgMovementRowMapper rowMapper;

    @Override protected String getTableName() { return "fg_movement_tbl"; }
    @Override protected Map<String, Object> getKeyParamMap(String id) { return Map.of("movement_id", id); }
    @SuppressWarnings("unchecked")
    @Override protected <T> BaseRowMapper<T> getRowMapper() { return (BaseRowMapper<T>) rowMapper; }
    @SuppressWarnings("unchecked")
    @Override protected <T> Class<T> getEntityClass() { return (Class<T>) FgMovementEntity.class; }

    public Flux<FgMovementEntity> findLedger(String productId, LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder("""
            SELECT m.*, p.name AS product_name
            FROM rm_material_schema.fg_movement_tbl m
            LEFT JOIN rm_material_schema.product_tbl p ON p.product_id = m.product_id
            WHERE 1=1
            """);
        if (productId != null && !productId.isEmpty())
            sql.append(" AND m.product_id = '").append(productId).append("'");
        if (from != null) sql.append(" AND m.movement_date >= '").append(from).append("'");
        if (to   != null) sql.append(" AND m.movement_date <= '").append(to).append("'");
        sql.append(" ORDER BY m.movement_date DESC, m.created_at DESC");
        return databaseClient.sql(sql.toString())
                .map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    public Mono<String> nextMovementId() {
        return databaseClient.sql("""
            SELECT s.n FROM generate_series(1,(SELECT COUNT(*)+1 FROM rm_material_schema.fg_movement_tbl)) AS s(n)
            WHERE NOT EXISTS (
                SELECT 1 FROM rm_material_schema.fg_movement_tbl
                WHERE movement_id = CONCAT('FGM-', LPAD(s.n::text,6,'0'))
            ) ORDER BY s.n LIMIT 1
            """)
                .map((row, meta) -> row.get(0, Long.class)).one()
                .map(n -> String.format("FGM-%06d", n));
    }
}