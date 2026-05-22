package com.hst.materialmgmt.entity.fgstock;

import java.time.LocalDate;
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
@Table(value = "fg_movement_tbl", schema = "rm_material_schema")
public class FgMovementEntity extends BaseEntity {
    @Id @Column("movement_id")   private String    movementId;
    @Column("product_id")        private String    productId;
    @Column("movement_type")     private String    movementType;
    @Column("quantity")          private Integer   quantity;
    @Column("reference_id")      private String    referenceId;
    @Column("notes")             private String    notes;
    @Column("movement_date")     private LocalDate movementDate;

    // Transient — joined from product_tbl
    @org.springframework.data.annotation.Transient
    private String productName;

    @Override public String getId() { return movementId; }
}