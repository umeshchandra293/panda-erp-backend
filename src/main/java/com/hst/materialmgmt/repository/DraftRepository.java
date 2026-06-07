package com.hst.materialmgmt.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.entity.DraftEntity;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Repository
public class DraftRepository {

    @Autowired private DatabaseClient databaseClient;

    public Mono<DraftEntity> findByTypeAndId(String draftType, String draftId) {
        return databaseClient.sql("""
            SELECT * FROM rm_material_schema.drafts_tbl
            WHERE draft_id = :draftId AND draft_type = :draftType
            """)
            .bind("draftId",   draftId)
            .bind("draftType", draftType)
            .map((row, meta) -> {
                DraftEntity e = new DraftEntity();
                e.setDraftId(row.get("draft_id",   String.class));
                e.setDraftType(row.get("draft_type", String.class));
                e.setDraftData(row.get("draft_data", String.class));
                return e;
            }).one();
    }

    public Mono<Void> upsert(String draftId, String draftType, String draftData) {
        return databaseClient.sql("""
            INSERT INTO rm_material_schema.drafts_tbl
              (draft_id, draft_type, draft_data, updated_at)
            VALUES
              (:draftId, :draftType, :draftData, :now)
            ON CONFLICT (draft_id) DO UPDATE
              SET draft_data = :draftData, updated_at = :now
            """)
            .bind("draftId",   draftId)
            .bind("draftType", draftType)
            .bind("draftData", draftData)
            .bind("now",       LocalDateTime.now())
            .fetch().rowsUpdated().then();
    }

    public Mono<Void> delete(String draftId) {
        return databaseClient.sql(
            "DELETE FROM rm_material_schema.drafts_tbl WHERE draft_id = :draftId")
            .bind("draftId", draftId)
            .fetch().rowsUpdated().then();
    }
}