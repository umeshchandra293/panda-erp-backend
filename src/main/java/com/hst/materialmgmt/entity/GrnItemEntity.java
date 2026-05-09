package com.hst.materialmgmt.entity;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data @EqualsAndHashCode(callSuper = true)
@AllArgsConstructor @NoArgsConstructor
@Accessors(chain = true) @SuperBuilder
@Table(value = "rm_grn_item_tbl", schema = "erp_finance_schema")
public class GrnItemEntity extends BaseEntity {

    @Id @Column("grn_item_id")   private String grnItemId;
    @Column("grn_id")            private String grnId;
    @Column("material_id")       private String materialId;
    @Column("ordered_qty")       private BigDecimal orderedQty;
    @Column("received_qty")      private BigDecimal receivedQty;
    @Column("unit_cost")         private BigDecimal unitCost;
    @Column("notes")             private String notes;

    @Override public String getId() { return grnItemId; }
}