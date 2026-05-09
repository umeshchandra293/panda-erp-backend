package com.hst.materialmgmt.entity;

import java.math.BigDecimal;

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
@Table(value = "rm_material_tbl", schema = "erp_finance_schema")
public class RawMaterialEntity extends BaseEntity {

    @Id
    @Column("material_id")
    private String materialId;

    @Column("material_name")
    private String materialName;

    @Column("description")
    private String description;

    @Column("category")
    private String category;

    @Column("uom")
    private String uom;

    @Column("hsn_sac_code")
    private String hsnSacCode;

    @Column("reorder_level")
    private BigDecimal reorderLevel;

    @Column("safety_stock_level")
    private BigDecimal safetyStockLevel;

    @Column("is_active")
    private Boolean isActive;

    @Override
    public String getId() {
        return materialId;
    }
}