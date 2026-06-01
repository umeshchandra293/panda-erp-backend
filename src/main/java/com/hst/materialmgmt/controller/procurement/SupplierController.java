package com.hst.materialmgmt.controller.procurement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.hst.api.SupplierApi;
import com.hst.api.model.Supplier;
import com.hst.materialmgmt.service.SupplierService;
import com.hst.materialmgmt.controller.BaseController; // ← ADD THIS LINE

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "Supplier API", description = "Endpoints for supplier operations")
public class SupplierController extends BaseController implements SupplierApi {

    @Autowired
    private SupplierService supplierService;

    @Override
    public Mono<ResponseEntity<Flux<Supplier>>> getAllSuppliers(
        Boolean isActive, String supplierCategory, ServerWebExchange exchange) {
        Flux<Supplier> flux = findAll(supplierService, exchange).cast(Supplier.class);
         return Mono.just(ResponseEntity.ok(flux));
}

    @Override
    public Mono<ResponseEntity<Supplier>> getSupplierByCode(String supplierCode, ServerWebExchange exchange) {
        return findByKey(supplierService, supplierCode, exchange)
            .cast(Supplier.class)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Supplier>> createSupplier(Mono<Supplier> supplier, ServerWebExchange exchange) {
        return create(supplierService, supplier.cast(Object.class), exchange)
            .cast(Supplier.class)
            .map(newSupplier -> ResponseEntity.status(HttpStatus.CREATED).body(newSupplier));
    }

    @Override
    public Mono<ResponseEntity<Supplier>> updateSupplier(String supplierCode, Mono<Supplier> supplier, ServerWebExchange exchange) {
        return update(supplierService, supplierCode, supplier.cast(Object.class), exchange)
            .cast(Supplier.class)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteSupplier(String supplierCode, ServerWebExchange exchange) {
        return delete(supplierService, supplierCode, exchange);
    }
}
