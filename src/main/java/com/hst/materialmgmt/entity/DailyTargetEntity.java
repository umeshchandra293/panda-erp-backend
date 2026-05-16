package com.hst.materialmgmt.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data @EqualsAndHashCode(callSuper = true)
@AllArgsConstructor @NoArgsConstructor
@Accessors(chain = true) @SuperBuilder
@Table(value = "sales_daily_target_tbl", schema = "erp_finance_schema")
public class DailyTargetEntity extends BaseEntity {

    @Id @Column("target_id")          private String targetId;
    @Column("salesman_id")            private String salesmanId;
    @Column("target_date")            private LocalDate targetDate;
    @Column("visit_target")           private Integer visitTarget;
    @Column("order_target")           private Integer orderTarget;
    @Column("collection_target")      private BigDecimal collectionTarget;

    @Override public String getId() { return targetId; }
}
