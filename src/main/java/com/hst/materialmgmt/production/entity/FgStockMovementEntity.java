package com.hst.materialmgmt.production.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import com.hst.materialmgmt.entity.BaseEntity;
import lombok.*;
import lombok.experimental.*;

@Data @EqualsAndHashCode(callSuper = true)
@AllArgsConstructor @NoArgsConstructor
@Accessors(chain = true) @SuperBuilder
@Table(value = "fg_stock_movement_tbl", schema = "erp_finance_schema")
public class FgStockMovementEntity extends BaseEntity {
    @Id @Column("movement_id")   private String movementId;
    @Column("product_id")        private String productId;
    @Column("movement_type")     private String movementType;
    @Column("quantity")          private BigDecimal quantity;
    @Column("reference_type")    private String referenceType;
    @Column("reference_id")      private String referenceId;
    @Column("movement_date")     private LocalDate movementDate;
    @Column("notes")             private String notes;
    @Override public String getId() { return movementId; }
}