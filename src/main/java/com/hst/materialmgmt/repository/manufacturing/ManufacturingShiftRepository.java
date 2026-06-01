package com.hst.materialmgmt.repository.manufacturing;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.entity.manufacturing.ManufacturingShiftEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.manufacturing.ManufacturingShiftRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ManufacturingShiftRepository extends ParentRepositoryImpl {

    @Autowired private ManufacturingShiftRowMapper rowMapper;

    @Override protected String getTableName() { return "manufacturing_shift_tbl"; }
    @Override protected Map<String, Object> getKeyParamMap(String id) { return Map.of("shift_id", id); }
    @SuppressWarnings("unchecked")
    @Override protected <T> BaseRowMapper<T> getRowMapper() { return (BaseRowMapper<T>) rowMapper; }
    @SuppressWarnings("unchecked")
    @Override protected <T> Class<T> getEntityClass() { return (Class<T>) ManufacturingShiftEntity.class; }

    public Flux<ManufacturingShiftEntity> findAllShifts() {
        return databaseClient.sql("""
            SELECT * FROM rm_material_schema.manufacturing_shift_tbl
            ORDER BY shift_date DESC, created_at DESC
            """)
                .map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    public Mono<String> nextShiftId() {
        return databaseClient.sql("""
            SELECT s.n FROM generate_series(1,(SELECT COUNT(*)+1 FROM rm_material_schema.manufacturing_shift_tbl)) AS s(n)
            WHERE NOT EXISTS (
                SELECT 1 FROM rm_material_schema.manufacturing_shift_tbl
                WHERE shift_id = CONCAT('SHIFT-', LPAD(s.n::text,6,'0'))
            ) ORDER BY s.n LIMIT 1
            """)
                .map((row, meta) -> row.get(0, Long.class)).one()
                .map(n -> String.format("SHIFT-%06d", n));
    }
}