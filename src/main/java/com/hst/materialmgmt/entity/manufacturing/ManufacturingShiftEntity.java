package com.hst.materialmgmt.entity.manufacturing;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import com.hst.materialmgmt.entity.BaseEntity;

@Data @EqualsAndHashCode(callSuper = true)
@AllArgsConstructor @NoArgsConstructor
@Accessors(chain = true) @SuperBuilder
@Table(value = "manufacturing_shift_tbl", schema = "rm_material_schema")
public class ManufacturingShiftEntity extends BaseEntity {
    @Id @Column("shift_id")       private String shiftId;
    @Column("shift_date")         private LocalDate shiftDate;
    @Column("shift_type")         private String shiftType;
    @Column("operator_name")      private String operatorName;
    @Column("status")             private String status;
    @Column("notes")              private String notes;
    @Column("total_units")        private Integer totalUnits;
    @Column("total_rejected")     private Integer totalRejected;
    @Override public String getId() { return shiftId; }
}