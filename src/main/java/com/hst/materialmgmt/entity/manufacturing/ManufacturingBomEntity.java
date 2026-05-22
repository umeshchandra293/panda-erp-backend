package com.hst.materialmgmt.entity.manufacturing;

import java.math.BigDecimal;
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
@Table(value = "manufacturing_bom_tbl", schema = "rm_material_schema")
public class ManufacturingBomEntity extends BaseEntity {
    @Id @Column("bom_id")         private String bomId;
    @Column("product_id")         private String productId;
    @Column("material_id")        private String materialId;
    @Column("qty_per_unit")       private BigDecimal qtyPerUnit;
    @Column("uom")                private String uom;
    @Column("notes")              private String notes;
    @Column("is_active")          private Boolean isActive;
    @Override public String getId() { return bomId; }
}