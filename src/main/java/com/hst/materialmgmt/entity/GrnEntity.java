package com.hst.materialmgmt.entity;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data @EqualsAndHashCode(callSuper = true)
@AllArgsConstructor @NoArgsConstructor
@Accessors(chain = true) @SuperBuilder
@Table(value = "rm_grn_tbl", schema = "rm_material_schema")
public class GrnEntity extends BaseEntity {

    @Id @Column("grn_id")          private String grnId;
    @Column("po_id")               private String poId;
    @Column("supplier_code")       private String supplierCode;
    @Column("received_date")       private LocalDate receivedDate;
    @Column("invoice_number")      private String invoiceNumber;
    @Column("status")              private String status;
    @Column("notes")               private String notes;

    @Override public String getId() { return grnId; }
}