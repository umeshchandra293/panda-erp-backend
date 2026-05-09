package com.hst.materialmgmt.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.hs.api.StockMovementApi;
import com.hs.api.model.StockMovement;
import com.hst.materialmgmt.service.StockMovementService;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@Tag(name = "Stock Movement API", description = "Inventory ledger operations")
public class StockMovementController implements StockMovementApi {

    private static final Logger log = LoggerFactory.getLogger(StockMovementController.class);

    private final StockMovementService service;

    public StockMovementController(StockMovementService service) {
        this.service = service;
    }

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

        // Generated signature passes a Flux. Collect to list, then batch-create.
        Mono<ResponseEntity<Flux<StockMovement>>> result = stockMovement
                .collectList()
                .map(list -> {
                    Flux<StockMovement> savedFlux = service.createBatch(list);
                    return ResponseEntity.status(HttpStatus.CREATED).body(savedFlux);
                })
                .doOnError(e -> log.error("Batch movement record failed", e));

        return result;
    }
}