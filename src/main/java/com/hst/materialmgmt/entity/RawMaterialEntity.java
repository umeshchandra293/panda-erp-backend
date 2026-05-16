package com.hst.materialmgmt.entity;

import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data @EqualsAndHashCode(callSuper = true)
@AllArgsConstructor @NoArgsConstructor
@Accessors(chain = true) @ToString @SuperBuilder
@Table(value = "rm_material_tbl", schema = "rm_material_schema")  // ← fixed schema
public class RawMaterialEntity extends BaseEntity {

    @Id @Column("material_id")
    private String materialId;

    @Column("material_name")
    private String materialName;

    @Column("description")
    private String description;

    @Column("category")       // ← now a plain string column
    private String category;

    @Column("uom")            // ← was unit_of_measure in DB, now uom
    private String uom;

    @Column("hsn_sac_code")   // ← new column
    private String hsnSacCode;

    @Column("reorder_level")
    private BigDecimal reorderLevel;

    @Column("safety_stock_level")
    private BigDecimal safetyStockLevel;

    @Column("is_active")
    private Boolean isActive;

    @Override
    public String getId() { return materialId; }
}