package com.hst.materialmgmt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.hs.api.PurchaseOrderApi;
import com.hs.api.model.PurchaseOrderSummary;
import com.hs.api.model.PurchaseOrderRequest;
import com.hst.materialmgmt.service.PurchaseOrderService;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// @CrossOrigin removed — handled globally by CorsConfig
@RestController
@RequestMapping("/api")
@Tag(name = "Purchase Order API")
public class PurchaseOrderController extends BaseController implements PurchaseOrderApi {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Override
    public Mono<ResponseEntity<Flux<PurchaseOrderSummary>>> getAllPurchaseOrders(
            ServerWebExchange exchange) {
        Flux<PurchaseOrderSummary> poFlux = findAll(purchaseOrderService, exchange)
                .cast(PurchaseOrderSummary.class);
        return Mono.just(ResponseEntity.ok(poFlux));
    }

    @Override
    public Mono<ResponseEntity<Void>> createPurchaseOrder(
            Mono<PurchaseOrderRequest> purchaseOrderRequest,
            ServerWebExchange exchange) {

        Mono<Object> genericPoMono = purchaseOrderRequest.cast(Object.class);

        return create(purchaseOrderService, genericPoMono, exchange)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).<Void>build())
                .doOnError(Throwable::printStackTrace);
    }
}