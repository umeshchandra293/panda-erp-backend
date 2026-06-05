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

    // ── Queries ───────────────────────────────────────────────────────────────

    public Flux<FgDispatchEntity> findAllDispatches() {
        return databaseClient.sql("""
            SELECT * FROM rm_material_schema.fg_dispatch_tbl
            ORDER BY dispatch_date DESC, created_at DESC
            """)
            .map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    public Mono<FgDispatchEntity> findDispatchById(String id) {
        return databaseClient.sql(
            "SELECT * FROM rm_material_schema.fg_dispatch_tbl WHERE dispatch_id = :id")
            .bind("id", id)
            .map((row, meta) -> rowMapper.apply(row, meta)).one();
    }

    // ── Settle ────────────────────────────────────────────────────────────────
    //
    // Key behaviours:
    //  1. amount_collected is ADDITIVE — each call adds to the running total
    //     so partial payments accumulate correctly across multiple settlements.
    //  2. status is set automatically:
    //       SETTLED  → when cumulative collected >= amount_to_collect
    //       PARTIAL  → when some payment received but not yet fully collected
    //  3. payment_mode is appended (e.g. first "CASH", second "UPI"
    //     becomes "CASH + UPI") so the history of modes is preserved.
    //  4. settled_at is only stamped on the first call that reaches SETTLED.

    public Mono<Void> settle(String dispatchId, BigDecimal amountCollected,
                              String paymentMode, String notes) {
        return databaseClient.sql("""
            UPDATE rm_material_schema.fg_dispatch_tbl
            SET amount_collected = amount_collected + :amountCollected,
                payment_mode     = CASE
                                     WHEN payment_mode IS NULL OR payment_mode = ''
                                       THEN :paymentMode
                                     WHEN :paymentMode IS NULL OR :paymentMode = ''
                                       THEN payment_mode
                                     WHEN position(:paymentMode IN payment_mode) > 0
                                       THEN payment_mode
                                     ELSE payment_mode || ' + ' || :paymentMode
                                   END,
                status           = CASE
                                     WHEN (amount_collected + :amountCollected) >= amount_to_collect
                                       THEN 'SETTLED'
                                     WHEN (amount_collected + :amountCollected) > 0
                                       THEN 'PARTIAL'
                                     ELSE status
                                   END,
                notes            = CASE
                                     WHEN :notes IS NULL OR :notes = ''
                                       THEN notes
                                     WHEN notes IS NULL OR notes = ''
                                       THEN :notes
                                     ELSE notes || ' | ' || :notes
                                   END,
                settled_at       = CASE
                                     WHEN (amount_collected + :amountCollected) >= amount_to_collect
                                       THEN :settledAt
                                     ELSE settled_at
                                   END,
                updated_at       = NOW()
            WHERE dispatch_id = :dispatchId
            """)
            .bind("dispatchId",      dispatchId)
            .bind("amountCollected", amountCollected)
            .bind("paymentMode",     paymentMode != null ? paymentMode : "CASH")
            .bind("notes",           notes       != null ? notes       : "")
            .bind("settledAt",       LocalDateTime.now())
            .fetch().rowsUpdated().then();
    }

    // ── Next ID ───────────────────────────────────────────────────────────────

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