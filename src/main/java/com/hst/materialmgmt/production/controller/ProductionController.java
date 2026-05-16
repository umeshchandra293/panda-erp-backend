package com.hst.materialmgmt.production.controller;

import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import com.hst.api.FgStockApi;
import com.hst.api.ProductionApi;
import com.hst.api.model.FgStockItem;
import com.hst.api.model.ProductionShift;
import com.hst.api.model.ProductionShiftRequest;
import com.hst.materialmgmt.production.service.ProductionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@Tag(name = "Production API")
public class ProductionController implements ProductionApi, FgStockApi {

    private final ProductionService productionService;

    public ProductionController(ProductionService productionService) {
        this.productionService = productionService;
    }

    @Override
    public Mono<ResponseEntity<Flux<ProductionShift>>> getShifts(
            LocalDate fromDate, LocalDate toDate, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(
                productionService.getShifts(fromDate, toDate)));
    }

    @Override
    public Mono<ResponseEntity<ProductionShift>> createShift(
            Mono<ProductionShiftRequest> request, ServerWebExchange exchange) {
        return request.flatMap(productionService::createShift)
                .map(s -> ResponseEntity.status(HttpStatus.CREATED).body(s));
    }

    @Override
    public Mono<ResponseEntity<Flux<FgStockItem>>> getFgStock(
            ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(productionService.getFgStock()));
    }
}
