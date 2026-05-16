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
@Table(value = "production_bom_tbl", schema = "erp_finance_schema")
public class ProductionBomEntity extends BaseEntity {
    @Id @Column("bom_id")        private String bomId;
    @Column("product_id")        private String productId;
    @Column("material_id")       private String materialId;
    @Column("qty_per_unit")      private BigDecimal qtyPerUnit;
    @Column("uom")               private String uom;
    @Override public String getId() { return bomId; }
}
