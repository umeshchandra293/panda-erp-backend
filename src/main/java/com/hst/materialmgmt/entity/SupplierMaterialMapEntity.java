package com.hst.materialmgmt.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ToString
@SuperBuilder
@Table(value = "rm_supplier_material_map_tbl", schema = "erp_finance_schema")
public class SupplierMaterialMapEntity extends BaseEntity {

    @Id
    @Column("mapping_id")
    private String mappingId;

    @Column("supplier_code")
    private String supplierCode;

    @Column("material_id")
    private String materialId;

    @Column("agreed_price")
    private BigDecimal agreedPrice;

    @Column("uom")
    private String uom;

    @Column("pack_size")
    private BigDecimal packSize;

    @Column("pack_uom")
    private String packUom;

    @Column("min_order_qty")
    private BigDecimal minOrderQty;

    @Column("hsn_sac_code")
    private String hsnSacCode;

    @Column("gst_rate")
    private BigDecimal gstRate;

    @Column("lead_time_days")
    private Integer leadTimeDays;

    @Column("currency_code")
    private String currencyCode;

    @Column("effective_date")
    private LocalDate effectiveDate;

    @Column("expiry_date")
    private LocalDate expiryDate;

    @Column("is_active")
    private Boolean isActive;

    @Override
    public String getId() {
        return mappingId;
    }
}
