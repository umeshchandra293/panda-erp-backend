package com.hst.materialmgmt.entity.fgstock;

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
@Table(value = "fg_stock_tbl", schema = "rm_material_schema")
public class FgStockEntity extends BaseEntity {

    @Id @Column("fg_id")      private String  fgId;
    @Column("product_id")     private String  productId;
    @Column("quantity")       private Integer quantity;

    // Transient — joined from product_tbl
    @org.springframework.data.annotation.Transient private String  productName;
    @org.springframework.data.annotation.Transient private String  sku;
    @org.springframework.data.annotation.Transient private String  unit;
    @org.springframework.data.annotation.Transient private Integer unitsPerBox;
    @org.springframework.data.annotation.Transient private Integer fullBoxes;
    @org.springframework.data.annotation.Transient private Integer looseUnits;

    @Override public String getId() { return fgId; }
}