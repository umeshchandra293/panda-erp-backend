package com.hst.materialmgmt.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.hs.api.HrSalesmanApi;
import com.hs.api.HrTargetApi;
import com.hs.api.model.DailyTarget;
import com.hs.api.model.SalesmanProfile;
import com.hs.api.model.SalesRoute;
import com.hst.materialmgmt.service.HrService;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@Tag(name = "HR API")
public class HrController implements HrSalesmanApi, HrTargetApi {

    private final HrService hrService;

    public HrController(HrService hrService) {
        this.hrService = hrService;
    }

    // ── Salesmen ───────────────────────────────────────────────────────

    @Override
    public Mono<ResponseEntity<Flux<SalesmanProfile>>> getAllSalesmen(
            Boolean isActive, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(hrService.getAllSalesmen(isActive)));
    }

    @Override
    public Mono<ResponseEntity<SalesmanProfile>> createSalesman(
            Mono<SalesmanProfile> salesmanProfile, ServerWebExchange exchange) {
        return salesmanProfile.flatMap(hrService::createSalesman)
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(p));
    }

    @Override
    public Mono<ResponseEntity<SalesmanProfile>> getSalesmanById(
            String salesmanId, ServerWebExchange exchange) {
        return hrService.getSalesmanById(salesmanId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<SalesmanProfile>> updateSalesman(
            String salesmanId, Mono<SalesmanProfile> salesmanProfile,
            ServerWebExchange exchange) {
        return salesmanProfile
                .flatMap(p -> hrService.updateSalesman(salesmanId, p))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<SalesRoute>>> getAllRoutes(
            ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(hrService.getAllRoutes()));
    }

    // ── Targets ────────────────────────────────────────────────────────

    @Override
    public Mono<ResponseEntity<Flux<DailyTarget>>> getTargets(
            String salesmanId, LocalDate fromDate, LocalDate toDate,
            ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(
                hrService.getTargets(salesmanId, fromDate, toDate)));
    }

    @Override
    public Mono<ResponseEntity<DailyTarget>> setTarget(
            Mono<DailyTarget> dailyTarget, ServerWebExchange exchange) {
        return dailyTarget.flatMap(hrService::setTarget)
                .map(t -> ResponseEntity.status(HttpStatus.CREATED).body(t));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteTarget(
            String targetId, ServerWebExchange exchange) {
        return hrService.deleteTarget(targetId)
                .then(Mono.just(ResponseEntity.<Void>noContent().build()));
    }
}