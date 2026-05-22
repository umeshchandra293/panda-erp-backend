package com.hst.materialmgmt.controller;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public Mono<ResponseEntity<Flux<StockMovement>>> getAllMovements(
            String materialId, String movementType,
            LocalDate fromDate, LocalDate toDate,
            ServerWebExchange exchange) {
        Flux<StockMovement> flux = service.findFiltered(materialId, movementType, fromDate, toDate);
        return Mono.just(ResponseEntity.ok(flux));
    }

    @Override
    public Mono<ResponseEntity<Flux<StockMovement>>> recordBatchMovements(
            Flux<StockMovement> stockMovement, ServerWebExchange exchange) {
        return stockMovement.collectList()
                .map(list -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(service.createBatch(list)))
                .doOnError(e -> log.error("Batch movement record failed", e));
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