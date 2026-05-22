package com.hst.materialmgmt.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data @EqualsAndHashCode(callSuper = true)
@AllArgsConstructor @NoArgsConstructor
@Accessors(chain = true) @SuperBuilder
@Table(value = "rm_stock_movement_tbl", schema = "erp_finance_schema")
public class StockMovementEntity extends BaseEntity {

    @Id @Column("movement_id")      private String movementId;
    @Column("material_id")          private String materialId;
    @Column("movement_type")        private String movementType;
    @Column("quantity")             private BigDecimal quantity;
    @Column("unit_cost")            private BigDecimal unitCost;
    @Column("movement_date")        private LocalDate movementDate;
    @Column("reference_type")       private String referenceType;
    @Column("reference_id")         private String referenceId;
    @Column("reason_code")          private String reasonCode;
    @Column("notes")                private String notes;

    @Override public String getId() { return movementId; }
}