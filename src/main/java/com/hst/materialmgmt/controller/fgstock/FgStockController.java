package com.hst.materialmgmt.controller.fgstock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hst.materialmgmt.entity.fgstock.FgDispatchEntity;
import com.hst.materialmgmt.entity.fgstock.FgDispatchItemEntity;
import com.hst.materialmgmt.entity.fgstock.FgMovementEntity;
import com.hst.materialmgmt.entity.fgstock.FgStockEntity;
import com.hst.materialmgmt.service.fgstock.FgStockService;
import com.hst.materialmgmt.service.fgstock.FgStockService.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt/fg-stock")
@Tag(name = "Finished Goods Stock API")
public class FgStockController {

    @Autowired private FgStockService service;

    // ── Stock ─────────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<Flux<FgStockEntity>> getAllStock() {
        return ResponseEntity.ok(service.getAllStock());
    }

    @GetMapping("/ledger")
    public ResponseEntity<Flux<FgMovementEntity>> getLedger(
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        return ResponseEntity.ok(service.getLedger(productId, fromDate, toDate));
    }

    // ── New multi-product dispatch with driver details ─────────────────────

    @PostMapping("/dispatch")
    public Mono<ResponseEntity<FgDispatchEntity>> dispatch(
            @RequestBody DispatchRequest req) {
        return service.createDispatch(req)
                .map(d -> ResponseEntity.status(HttpStatus.CREATED).body(d))
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.<FgDispatchEntity>badRequest().build()));
    }

    // ── Dispatch list ─────────────────────────────────────────────────────────

    @GetMapping("/dispatches")
    public ResponseEntity<Flux<FgDispatchEntity>> getAllDispatches() {
        return ResponseEntity.ok(service.getAllDispatches());
    }

    @GetMapping("/dispatches/{dispatchId}/items")
    public ResponseEntity<Flux<FgDispatchItemEntity>> getDispatchItems(
            @PathVariable String dispatchId) {
        return ResponseEntity.ok(service.getDispatchItems(dispatchId));
    }

    // ── Settle ────────────────────────────────────────────────────────────────

@PostMapping("/dispatches/{dispatchId}/settle")
public Mono<ResponseEntity<Void>> settle(
        @PathVariable String dispatchId,
        @RequestBody SettleRequest req) {
    return service.settle(dispatchId, req)
            .<ResponseEntity<Void>>thenReturn(ResponseEntity.<Void>ok().build())
            .doOnError(e -> System.err.println(
                    "[SETTLE ERROR] dispatchId=" + dispatchId +
                    " | " + e.getClass().getSimpleName() +
                    " | " + e.getMessage()))
            .onErrorResume(e -> Mono.just(
                    ResponseEntity.<Void>status(
                            e.getMessage() != null && e.getMessage().contains("not found") ? 404 : 400
                    ).build()));
}

    // ── Legacy single-product (kept for backward compat) ─────────────────────

    @PostMapping("/dispatch-legacy")
    public Mono<ResponseEntity<Void>> dispatchLegacy(
            @RequestBody LegacyDispatchRequest req) {
        return service.dispatch(req.productId(), req.quantity(), req.referenceId(), req.notes())
                .<ResponseEntity<Void>>thenReturn(ResponseEntity.<Void>status(HttpStatus.CREATED).build())
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(ResponseEntity.<Void>badRequest().build()));
    }

    public record LegacyDispatchRequest(
        String productId,
        int    quantity,
        String referenceId,
        String notes
    ) {}
}