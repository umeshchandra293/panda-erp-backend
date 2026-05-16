package com.hst.materialmgmt.repository;

import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.entity.DailyTargetEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.DailyTargetRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class DailyTargetRepository extends ParentRepositoryImpl {

    @Autowired private DailyTargetRowMapper rowMapper;

    @Override protected String getTableName() { return "sales_daily_target_tbl"; }
    @Override protected Map<String, Object> getKeyParamMap(String id) {
        return Map.of("target_id", id);
    }
    @SuppressWarnings("unchecked")
    @Override protected <T> BaseRowMapper<T> getRowMapper() { return (BaseRowMapper<T>) rowMapper; }
    @SuppressWarnings("unchecked")
    @Override protected <T> Class<T> getEntityClass() { return (Class<T>) DailyTargetEntity.class; }

    public Flux<DailyTargetEntity> findFiltered(
            String salesmanId, LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM erp_finance_schema.sales_daily_target_tbl WHERE 1=1");
        if (salesmanId != null) sql.append(" AND salesman_id = :salesmanId");
        if (from != null)       sql.append(" AND target_date >= :fromDate");
        if (to != null)         sql.append(" AND target_date <= :toDate");
        sql.append(" ORDER BY target_date DESC");

        var spec = databaseClient.sql(sql.toString());
        if (salesmanId != null) spec = spec.bind("salesmanId", salesmanId);
        if (from != null)       spec = spec.bind("fromDate", from);
        if (to != null)         spec = spec.bind("toDate", to);

        return spec.map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    /** Check if target already exists for this salesman+date */
    public Mono<DailyTargetEntity> findByDateAndSalesman(
            String salesmanId, LocalDate date) {
        String sql = "SELECT * FROM erp_finance_schema.sales_daily_target_tbl " +
                     "WHERE salesman_id = :salesmanId AND target_date = :date";
        return databaseClient.sql(sql)
                .bind("salesmanId", salesmanId)
                .bind("date", date)
                .map((row, meta) -> rowMapper.apply(row, meta)).one();
    }

    public Mono<Void> updateTarget(DailyTargetEntity e) {
        String sql = """
            UPDATE erp_finance_schema.sales_daily_target_tbl
            SET visit_target = :visitTarget,
                order_target = :orderTarget,
                collection_target = :collectionTarget,
                updated_at = CURRENT_TIMESTAMP
            WHERE target_id = :targetId
            """;
        return databaseClient.sql(sql)
                .bind("visitTarget",       e.getVisitTarget())
                .bind("orderTarget",       e.getOrderTarget())
                .bind("collectionTarget",  e.getCollectionTarget())
                .bind("targetId",          e.getTargetId())
                .fetch().rowsUpdated().then();
    }
}
