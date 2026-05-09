package com.hst.materialmgmt.production.entity;

import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import com.hst.materialmgmt.entity.BaseEntity;
import lombok.*;
import lombok.experimental.*;

@Data @EqualsAndHashCode(callSuper = true)
@AllArgsConstructor @NoArgsConstructor
@Accessors(chain = true) @SuperBuilder
@Table(value = "production_batch_tbl", schema = "erp_finance_schema")
public class ProductionBatchEntity extends BaseEntity {
    @Id @Column("batch_id")      private String batchId;
    @Column("shift_id")          private String shiftId;
    @Column("product_id")        private String productId;
    @Column("planned_qty")       private BigDecimal plannedQty;
    @Column("actual_qty")        private BigDecimal actualQty;
    @Column("rejected_qty")      private BigDecimal rejectedQty;
    @Column("notes")             private String notes;
    @Override public String getId() { return batchId; }
}