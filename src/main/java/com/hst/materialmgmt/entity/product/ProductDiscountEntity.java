package com.hst.materialmgmt.entity.product;

import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(value = "product_discount_tbl", schema = "rm_material_schema")
public class ProductDiscountEntity extends BaseEntity {

    @Id @Column("discount_id")
    private String discountId;
    @Column("product_id")   private String productId;
    @Column("name")         private String name;
    @Column("discount_type") private String discountType;
    @Column("value")        private BigDecimal value;
    @Column("min_quantity") private Integer minQuantity;
    @Column("effective_date") private LocalDate effectiveDate;
    @Column("end_date")     private LocalDate endDate;

    @Override public String getId() { return discountId; }
}
