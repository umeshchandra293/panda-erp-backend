package com.hst.materialmgmt.production.entity;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import com.hst.materialmgmt.entity.BaseEntity;
import lombok.*;
import lombok.experimental.*;

@Data @EqualsAndHashCode(callSuper = true)
@AllArgsConstructor @NoArgsConstructor
@Accessors(chain = true) @SuperBuilder
@Table(value = "production_shift_tbl", schema = "erp_finance_schema")
public class ProductionShiftEntity extends BaseEntity {
    @Id @Column("shift_id")      private String shiftId;
    @Column("shift_date")        private LocalDate shiftDate;
    @Column("shift_type")        private String shiftType;
    @Column("operator_name")     private String operatorName;
    @Column("notes")             private String notes;
    @Column("status")            private String status;
    @Override public String getId() { return shiftId; }
}