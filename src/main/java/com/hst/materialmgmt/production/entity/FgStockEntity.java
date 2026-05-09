package com.hst.materialmgmt.production.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import com.hst.materialmgmt.entity.BaseEntity;
import lombok.*;
import lombok.experimental.*;

@Data @EqualsAndHashCode(callSuper = true)
@AllArgsConstructor @NoArgsConstructor
@Accessors(chain = true) @SuperBuilder
@Table(value = "fg_stock_tbl", schema = "erp_finance_schema")
public class FgStockEntity extends BaseEntity {
    @Id @Column("fg_id")         private String fgId;
    @Column("product_id")        private String productId;
    @Column("quantity")          private BigDecimal quantity;
    @Column("last_updated")      private LocalDateTime lastUpdated;
    @Override public String getId() { return fgId; }
}