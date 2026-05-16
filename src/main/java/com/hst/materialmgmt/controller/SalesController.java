package com.hst.materialmgmt.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.hst.api.SalesDashboardApi;
import com.hst.api.SalesOrderApi;
import com.hst.api.SalesPaymentApi;
import com.hst.api.SalesProductApi;
import com.hst.api.SalesRetailerApi;
import com.hst.api.SalesSalesmanApi;
import com.hst.api.SalesVisitApi;
import com.hst.api.model.Retailer;
import com.hst.api.model.RetailerLedger;
import com.hst.api.model.Salesman;
import com.hst.api.model.SalesKpis;
import com.hst.api.model.SalesOrder;
import com.hst.api.model.SalesPayment;
import com.hst.api.model.SalesProduct;
import com.hst.api.model.SalesVisit;
import com.hst.api.model.SalesmanActivityRow;
import com.hst.api.model.SalesmanDaySummary;
import com.hst.api.model.UpdateOrderStatusRequest;
import com.hst.materialmgmt.service.SalesService;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@Tag(name = "Sales API")
public class SalesController implements
        SalesRetailerApi, SalesProductApi, SalesVisitApi,
        SalesOrderApi, SalesPaymentApi, SalesSalesmanApi, SalesDashboardApi {

    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    // ── Retailers ─────────────────────────────────────────────────────

    @Override
    public Mono<ResponseEntity<Flux<Retailer>>> getAllRetailers(
            String salesmanId, String area, Boolean isActive, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(
                salesService.getRetailers(salesmanId, area, isActive)));
    }

    @Override
    public Mono<ResponseEntity<Retailer>> createRetailer(
            Mono<Retailer> retailer, ServerWebExchange exchange) {
        return retailer.flatMap(salesService::createRetailer)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r));
    }

    @Override
    public Mono<ResponseEntity<Retailer>> getRetailerById(
            String retailerId, ServerWebExchange exchange) {
        return salesService.getRetailerById(retailerId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Retailer>> updateRetailer(
            String retailerId, Mono<Retailer> retailer, ServerWebExchange exchange) {
        return retailer.flatMap(r -> salesService.updateRetailer(retailerId, r))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<RetailerLedger>> getRetailerLedger(
            String retailerId, ServerWebExchange exchange) {
        return salesService.getRetailerLedger(retailerId).map(ResponseEntity::ok);
    }

    // ── Products ───────────────────────────────────────────────────────

    @Override
    public Mono<ResponseEntity<Flux<SalesProduct>>> getAllProducts(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(salesService.getAllProducts()));
    }

    @Override
    public Mono<ResponseEntity<Flux<SalesProduct>>> getProductsForRetailer(
            String retailerId, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(salesService.getProductsForRetailer(retailerId)));
    }

    // ── Visits ─────────────────────────────────────────────────────────

    @Override
    public Mono<ResponseEntity<Flux<SalesVisit>>> getAllVisits(
            String salesmanId, String retailerId,
            LocalDate fromDate, LocalDate toDate, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(
                salesService.getVisits(salesmanId, retailerId, fromDate, toDate)));
    }

    @Override
    public Mono<ResponseEntity<SalesVisit>> createVisit(
            Mono<SalesVisit> salesVisit, ServerWebExchange exchange) {
        return salesVisit.flatMap(salesService::createVisit)
                .map(v -> ResponseEntity.status(HttpStatus.CREATED).body(v));
    }

    // ── Orders ─────────────────────────────────────────────────────────

    @Override
    public Mono<ResponseEntity<Flux<SalesOrder>>> getAllOrders(
            String salesmanId, String retailerId,
            LocalDate fromDate, LocalDate toDate, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(
                salesService.getOrders(salesmanId, retailerId, fromDate, toDate)));
    }

    @Override
    public Mono<ResponseEntity<SalesOrder>> createOrder(
            Mono<SalesOrder> salesOrder, ServerWebExchange exchange) {
        return salesOrder.flatMap(salesService::createOrder)
                .map(o -> ResponseEntity.status(HttpStatus.CREATED).body(o))
                .onErrorResume(e -> {
                    if (e.getMessage() != null &&
                            e.getMessage().startsWith("CREDIT_LIMIT_EXCEEDED")) {
                        return Mono.just(
                                ResponseEntity.unprocessableEntity().<SalesOrder>build());
                    }
                    return Mono.error(e);
                });
    }

    @Override
    public Mono<ResponseEntity<SalesOrder>> updateOrderStatus(
            String orderId,
            Mono<UpdateOrderStatusRequest> updateOrderStatusRequest,
            ServerWebExchange exchange) {
        return updateOrderStatusRequest
                .flatMap(req -> salesService.updateOrderStatus(orderId, req.getStatus().name()))
                .map(ResponseEntity::ok);
    }

    // ── Payments ───────────────────────────────────────────────────────

    @Override
    public Mono<ResponseEntity<Flux<SalesPayment>>> getAllPayments(
            String salesmanId, String retailerId,
            LocalDate fromDate, LocalDate toDate, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(
                salesService.getPayments(salesmanId, retailerId, fromDate, toDate)));
    }

    @Override
    public Mono<ResponseEntity<SalesPayment>> createPayment(
            Mono<SalesPayment> salesPayment, ServerWebExchange exchange) {
        return salesPayment.flatMap(salesService::createPayment)
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(p));
    }

    // ── Salesman ───────────────────────────────────────────────────────

    @Override
    public Mono<ResponseEntity<Salesman>> getSalesmanByUsername(
            String username, ServerWebExchange exchange) {
        return salesService.getSalesmanByUsername(username)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<SalesmanDaySummary>> getSalesmanToday(
            String salesmanId, ServerWebExchange exchange) {
        return salesService.getSalesmanToday(salesmanId).map(ResponseEntity::ok);
    }

    // ── Dashboard ──────────────────────────────────────────────────────

    @Override
    public Mono<ResponseEntity<Flux<SalesmanActivityRow>>> getSalesActivity(
            LocalDate date, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(salesService.getSalesActivity(date)));
    }

    @Override
    public Mono<ResponseEntity<SalesKpis>> getSalesKpis(ServerWebExchange exchange) {
        return salesService.getKpis().map(ResponseEntity::ok);
    }
}
