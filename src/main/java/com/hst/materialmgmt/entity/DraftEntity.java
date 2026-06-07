package com.hst.materialmgmt.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor
@Table(value = "drafts_tbl", schema = "rm_material_schema")
public class DraftEntity {
    @Id @Column("draft_id")   private String draftId;
    @Column("draft_type")     private String draftType;
    @Column("draft_data")     private String draftData;
    @Column("updated_at")     private LocalDateTime updatedAt;
}