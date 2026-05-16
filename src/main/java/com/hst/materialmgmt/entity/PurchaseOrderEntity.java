package com.hst.materialmgmt.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "rm_purchase_order_tbl", schema = "erp_finance_schema")
public class PurchaseOrderEntity extends BaseEntity {

    @Id
    @Column("po_id")
    private String poId;

    @Column("supplier_code")
    private String supplierCode;

    @Column("order_date")
    private LocalDate orderDate;

    @Column("expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    @Column("notes")
    private String notes;

    @Column("total_amount")
    private Double totalAmount;

    @Column("status")
    private String status;

    // ⚠️ itemEntities REMOVED — R2DBC cannot serialize entity collections.
    // Line items are handled separately by BaseServiceImpl via link tables.

    @Override
    public String getId() {
        return this.poId;
    }
}
