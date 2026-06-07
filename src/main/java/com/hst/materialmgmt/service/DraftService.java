// com/hst/materialmgmt/service/DraftService.java
package com.hst.materialmgmt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hst.materialmgmt.repository.DraftRepository;
import reactor.core.publisher.Mono;

@Service
public class DraftService {

    @Autowired private DraftRepository draftRepo;

    public Mono<String> getDraft(String draftId) {
        return draftRepo.findByTypeAndId("MFG_SHIFT", draftId)
                .map(d -> d.getDraftData());
    }

    public Mono<Void> saveDraft(String draftId, String draftData) {
        return draftRepo.upsert(draftId, "MFG_SHIFT", draftData);
    }

    public Mono<Void> deleteDraft(String draftId) {
        return draftRepo.delete(draftId);
    }
}