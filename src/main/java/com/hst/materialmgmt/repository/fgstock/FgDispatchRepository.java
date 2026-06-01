package com.hst.materialmgmt.repository.fgstock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.entity.fgstock.FgDispatchEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.fgstock.FgDispatchRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class FgDispatchRepository extends ParentRepositoryImpl {

    @Autowired private FgDispatchRowMapper rowMapper;

    @Override protected String getTableName() { return "fg_dispatch_tbl"; }
    @Override protected Map<String, Object> getKeyParamMap(String id) {
        return Map.of("dispatch_id", id);
    }
    @SuppressWarnings("unchecked")
    @Override protected <T> BaseRowMapper<T> getRowMapper() { return (BaseRowMapper<T>) rowMapper; }
    @SuppressWarnings("unchecked")
    @Override protected <T> Class<T> getEntityClass() { return (Class<T>) FgDispatchEntity.class; }

    // Renamed to avoid conflict with ParentRepository.findAll() → Flux<Object>
    public Flux<FgDispatchEntity> findAllDispatches() {
        return databaseClient.sql("""
            SELECT * FROM rm_material_schema.fg_dispatch_tbl
            ORDER BY dispatch_date DESC, created_at DESC
            """)
            .map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    // Renamed to avoid conflict with ParentRepository.findById() → Mono<Object>
    public Mono<FgDispatchEntity> findDispatchById(String id) {
        return databaseClient.sql(
            "SELECT * FROM rm_material_schema.fg_dispatch_tbl WHERE dispatch_id = :id")
            .bind("id", id)
            .map((row, meta) -> rowMapper.apply(row, meta)).one();
    }

    public Mono<Void> settle(String dispatchId, BigDecimal amountCollected,
                              String paymentMode, String notes) {
        return databaseClient.sql("""
            UPDATE rm_material_schema.fg_dispatch_tbl
            SET status = 'SETTLED',
                amount_collected = :amountCollected,
                payment_mode = :paymentMode,
                notes = COALESCE(NULLIF(:notes, ''), notes),
                settled_at = :settledAt,
                updated_at = NOW()
            WHERE dispatch_id = :dispatchId
            """)
            .bind("dispatchId",      dispatchId)
            .bind("amountCollected", amountCollected)
            .bind("paymentMode",     paymentMode != null ? paymentMode : "CASH")
            .bind("notes",           notes != null ? notes : "")
            .bind("settledAt",       LocalDateTime.now())
            .fetch().rowsUpdated().then();
    }

    public Mono<String> nextDispatchId() {
        return databaseClient.sql("""
            SELECT s.n FROM generate_series(1,(SELECT COUNT(*)+1 FROM rm_material_schema.fg_dispatch_tbl)) AS s(n)
            WHERE NOT EXISTS (
                SELECT 1 FROM rm_material_schema.fg_dispatch_tbl
                WHERE dispatch_id = CONCAT('DSP-', LPAD(s.n::text,6,'0'))
            ) ORDER BY s.n LIMIT 1
            """)
            .map((row, meta) -> row.get(0, Long.class)).one()
            .map(n -> String.format("DSP-%06d", n));
    }
}