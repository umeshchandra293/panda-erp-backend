package com.hst.materialmgmt.entity.product;

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
@Table(value = "product_price_tbl", schema = "rm_material_schema")
public class ProductPriceEntity extends BaseEntity {

    @Id @Column("product_price_id")
    private String productPriceId;
    @Column("product_id")       private String productId;
    @Column("base_unit_price")  private Double baseUnitPrice;
    @Column("currency_code")    private String currencyCode;
    @Column("effective_date")   private LocalDate effectiveDate;
    @Column("end_date")         private LocalDate endDate;

    @Override public String getId() { return productPriceId; }
}
