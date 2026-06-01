package com.hst.materialmgmt.entity.product;

import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import com.hst.materialmgmt.entity.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data @EqualsAndHashCode(callSuper = true)
@AllArgsConstructor @NoArgsConstructor
@Accessors(chain = true) @ToString @SuperBuilder
@Table(value = "product_tbl", schema = "rm_material_schema")
public class ProductEntity extends BaseEntity {
    @Id @Column("product_id")          private String     productId;
    @Column("sku")                     private String     sku;
    @Column("name")                    private String     name;
    @Column("category_id")             private String     categoryId;
    @Column("uom")                     private String     uom;
    @Column("is_active")               private Boolean    isActive;
    @Column("units_per_box")           private Integer    unitsPerBox;
    @Column("cost_per_case")           private BigDecimal costPerCase;
    @Column("selling_price_per_case")  private BigDecimal sellingPricePerCase;

    @Override public String getId() { return productId; }
}