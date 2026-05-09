package com.hst.materialmgmt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.hs.api.SupplierApi;
import com.hs.api.model.Supplier;
import com.hst.materialmgmt.service.SupplierService;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/finance")
@Tag(name = "Supplier API", description = "Endpoints for supplier operations")
public class SupplierController extends BaseController implements SupplierApi {

    private static final Logger log = LoggerFactory.getLogger(SupplierController.class);

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteSupplier(
            String supplierCode, ServerWebExchange exchange) {
        return delete(supplierService, supplierCode, exchange);
    }

    @Override
    public Mono<ResponseEntity<Flux<Supplier>>> getAllSuppliers(
            Boolean isActive, String category, ServerWebExchange exchange) {

        Flux<Supplier> supplierFlux = findAll(supplierService, exchange)
                .cast(Supplier.class)
                .filter(s -> isActive == null || isActive.equals(s.getIsActive()))
                .filter(s -> category == null
                        || (s.getSupplierCategory() != null
                                && category.equalsIgnoreCase(s.getSupplierCategory().name())));

        return Mono.just(ResponseEntity.ok(supplierFlux));
    }

    @Override
    public Mono<ResponseEntity<Supplier>> getSupplierByCode(
            String supplierCode, ServerWebExchange exchange) {
        return findByKey(supplierService, supplierCode, exchange)
                .cast(Supplier.class)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Supplier>> createSupplier(
            Mono<Supplier> supplier, ServerWebExchange exchange) {

        Mono<Object> genericMono = supplier.cast(Object.class);

        return create(supplierService, genericMono, exchange)
                .cast(Supplier.class)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved))
                .doOnError(e -> log.error("Failed to create supplier", e));
    }

    @Override
    public Mono<ResponseEntity<Supplier>> updateSupplier(
            String supplierCode, Mono<Supplier> supplier, ServerWebExchange exchange) {

        Mono<Object> genericMono = supplier
                .map(s -> {
                    s.setSupplierCode(supplierCode);
                    return (Object) s;
                });

        return update(supplierService, supplierCode, genericMono, exchange)
                .cast(Supplier.class)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnError(e -> log.error("Failed to update supplier {}", supplierCode, e));
    }
}