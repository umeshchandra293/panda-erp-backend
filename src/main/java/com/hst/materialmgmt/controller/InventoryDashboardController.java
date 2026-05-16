package com.hst.materialmgmt.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.hst.api.InventoryDashboardApi;
import com.hst.api.model.DashboardSummary;
import com.hst.api.model.TrendPoint;
import com.hst.api.model.WastageReason;
import com.hst.materialmgmt.service.StockMovementService;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@Tag(name = "Inventory Dashboard API")
public class InventoryDashboardController implements InventoryDashboardApi {

    private final StockMovementService service;

    public InventoryDashboardController(StockMovementService service) {
        this.service = service;
    }

    @Override
    public Mono<ResponseEntity<DashboardSummary>> getDashboardSummary(
            LocalDate fromDate, LocalDate toDate, ServerWebExchange exchange) {

        // Default range: last 30 days
        LocalDate to   = (toDate   != null) ? toDate   : LocalDate.now();
        LocalDate from = (fromDate != null) ? fromDate : to.minusDays(29);

        return service.getDashboardSummary(from, to)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<TrendPoint>>> getMovementTrend(
            Integer days, ServerWebExchange exchange) {
        int d = (days != null && days > 0) ? days : 30;
        return Mono.just(ResponseEntity.ok(service.getTrend(d)));
    }

    @Override
    public Mono<ResponseEntity<Flux<WastageReason>>> getWastageBreakdown(
            Integer days, ServerWebExchange exchange) {
        int d = (days != null && days > 0) ? days : 30;
        return Mono.just(ResponseEntity.ok(service.getWastageBreakdown(d)));
    }
}
