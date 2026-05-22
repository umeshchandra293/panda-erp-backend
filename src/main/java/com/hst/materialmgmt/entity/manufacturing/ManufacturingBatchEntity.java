package com.hst.materialmgmt.entity.manufacturing;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import com.hst.materialmgmt.entity.BaseEntity;

@Data @EqualsAndHashCode(callSuper = true)
@AllArgsConstructor @NoArgsConstructor
@Accessors(chain = true) @SuperBuilder
@Table(value = "manufacturing_batch_tbl", schema = "rm_material_schema")
public class ManufacturingBatchEntity extends BaseEntity {
    @Id @Column("batch_id")       private String batchId;
    @Column("shift_id")           private String shiftId;
    @Column("product_id")         private String productId;
    @Column("planned_qty")        private Integer plannedQty;
    @Column("actual_qty")         private Integer actualQty;
    @Column("rejected_qty")       private Integer rejectedQty;
    @Column("notes")              private String notes;
    @Override public String getId() { return batchId; }
}