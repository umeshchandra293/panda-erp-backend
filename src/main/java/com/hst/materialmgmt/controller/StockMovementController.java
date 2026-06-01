package com.hst.materialmgmt.controller;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import com.hst.api.StockMovementApi;
import com.hst.api.model.DashboardSummary;
import com.hst.api.model.StockMovement;
import com.hst.api.model.TrendPoint;
import com.hst.api.model.WastageReason;
import com.hst.materialmgmt.service.StockMovementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "Stock Movement API", description = "Inventory ledger operations")
public class StockMovementController implements StockMovementApi {

    private static final Logger log = LoggerFactory.getLogger(StockMovementController.class);

    @Autowired private StockMovementService service;

    // ── Original OpenAPI endpoints (/stock-movements) ────────────────────────

    @Override
    public Mono<ResponseEntity<Flux<StockMovement>>> getAllMovements(
            String materialId, String movementType,
            LocalDate fromDate, LocalDate toDate,
            ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(
                service.findFiltered(materialId, movementType, fromDate, toDate)));
    }

    @Override
    public Mono<ResponseEntity<Flux<StockMovement>>> recordBatchMovements(
            Flux<StockMovement> stockMovement, ServerWebExchange exchange) {
        return stockMovement.collectList()
                .map(list -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(service.createBatch(list)))
                .doOnError(e -> log.error("Batch movement record failed", e));
    }

    // ── Frontend alias: GET /material/mgmt/inventory/movements ───────────────

    @GetMapping("/inventory/movements")
    public Mono<ResponseEntity<Flux<StockMovement>>> getMovements(
            @RequestParam(required = false) String materialId,
            @RequestParam(required = false) String movementType,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return Mono.just(ResponseEntity.ok(
                service.findFiltered(materialId, movementType, fromDate, toDate)));
    }

    // ── Frontend alias: POST /material/mgmt/inventory/movements ─────────────
    // Used by ScrapEntryPage and InventoryPage adjust modal

    @PostMapping("/inventory/movements")
    public Mono<ResponseEntity<StockMovement>> createMovement(
            @RequestBody StockMovement movement) {
        return service.createBatch(java.util.List.of(movement))
                .next()
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved))
                .doOnError(e -> log.error("Movement creation failed: {}", e.getMessage()));
    }

    // ── Dashboard endpoints ───────────────────────────────────────────────────

    @GetMapping("/inventory/dashboard")
    public Mono<ResponseEntity<DashboardSummary>> getDashboard(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        LocalDate from = fromDate != null ? LocalDate.parse(fromDate) : LocalDate.now().minusDays(30);
        LocalDate to   = toDate   != null ? LocalDate.parse(toDate)   : LocalDate.now();
        return service.getDashboardSummary(from, to).map(ResponseEntity::ok);
    }

    @GetMapping("/inventory/trend")
    public ResponseEntity<Flux<TrendPoint>> getTrend(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(service.getTrend(days));
    }

    @GetMapping("/inventory/wastage-breakdown")
    public ResponseEntity<Flux<WastageReason>> getWastageBreakdown(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(service.getWastageBreakdown(days));
    }
}