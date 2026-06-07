package com.hst.materialmgmt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hst.materialmgmt.repository.DraftRepository;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/material/mgmt/drafts")
public class DraftController {

    @Autowired private DraftRepository draftRepo;

    @GetMapping("/{draftId}")
    public Mono<ResponseEntity<Map<String,String>>> getDraft(@PathVariable String draftId) {
        return draftRepo.findByTypeAndId("MFG_SHIFT", draftId)
            .map(d -> ResponseEntity.ok(Map.of("draftData", d.getDraftData())))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/{draftId}")
    public Mono<ResponseEntity<Void>> saveDraft(
            @PathVariable String draftId,
            @RequestBody Map<String, String> body) {
        return draftRepo.upsert(draftId, "MFG_SHIFT", body.get("draftData"))
            .then(Mono.just(ResponseEntity.<Void>ok().build()));
    }

    @DeleteMapping("/{draftId}")
    public Mono<ResponseEntity<Void>> deleteDraft(@PathVariable String draftId) {
        return draftRepo.delete(draftId)
            .then(Mono.just(ResponseEntity.<Void>noContent().build()));
    }
}