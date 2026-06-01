package com.hst.materialmgmt.entity.fgstock;

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
@Table(value = "fg_dispatch_item_tbl", schema = "rm_material_schema")
public class FgDispatchItemEntity extends BaseEntity {

    @Id @Column("dispatch_item_id")        private String     dispatchItemId;
    @Column("dispatch_id")                 private String     dispatchId;
    @Column("product_id")                  private String     productId;
    @Column("cases_dispatched")            private Integer    casesDispatched;
    @Column("bottles_dispatched")          private Integer    bottlesDispatched;
    @Column("cases_returned")              private Integer    casesReturned;
    @Column("bottles_returned")            private Integer    bottlesReturned;
    @Column("selling_price_per_case")      private BigDecimal sellingPricePerCase;

    @Override public String getId() { return dispatchItemId; }
}