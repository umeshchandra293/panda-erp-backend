package com.hst.materialmgmt.production.repository;

import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.production.entity.ProductionShiftEntity;
import com.hst.materialmgmt.production.rowMapper.ProductionShiftRowMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ProductionShiftRepository extends ParentRepositoryImpl {

    @Autowired private ProductionShiftRowMapper rowMapper;

    @Override protected String getTableName() { return "production_shift_tbl"; }
    @Override protected Map<String, Object> getKeyParamMap(String id) {
        return Map.of("shift_id", id);
    }
    @SuppressWarnings("unchecked")
    @Override protected <T> BaseRowMapper<T> getRowMapper() { return (BaseRowMapper<T>) rowMapper; }
    @SuppressWarnings("unchecked")
    @Override protected <T> Class<T> getEntityClass() {
        return (Class<T>) ProductionShiftEntity.class;
    }

    public Flux<ProductionShiftEntity> findByDateRange(LocalDate from, LocalDate to) {
        String sql = """
            SELECT * FROM erp_finance_schema.production_shift_tbl
            WHERE shift_date BETWEEN :from AND :to
            ORDER BY shift_date DESC, shift_type
            """;
        return databaseClient.sql(sql)
                .bind("from", from).bind("to", to)
                .map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    public Mono<String> nextShiftId() {
        return databaseClient
                .sql("SELECT nextval('erp_finance_schema.shift_seq')")
                .map((row, meta) -> row.get(0, Long.class)).one()
                .map(n -> String.format("SHF-%06d", n));
    }
}
