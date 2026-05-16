package com.hst.materialmgmt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.hst.api.SupplierMaterialApi;
import com.hst.api.model.SupplierMaterialMapping;
import com.hst.materialmgmt.service.SupplierMaterialMapService;
import com.hst.materialmgmt.service.SupplierMaterialMapService.DuplicateMappingException;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/finance")
@Tag(name = "Supplier Material API", description = "Manage supplier-material mappings")
public class SupplierMaterialController implements SupplierMaterialApi {

    private static final Logger log = LoggerFactory.getLogger(SupplierMaterialController.class);

    private final SupplierMaterialMapService service;

    public SupplierMaterialController(SupplierMaterialMapService service) {
        this.service = service;
    }

    @Override
    public Mono<ResponseEntity<Flux<SupplierMaterialMapping>>> getSupplierMaterials(
            String supplierCode, Boolean isActive, ServerWebExchange exchange) {

        Flux<SupplierMaterialMapping> flux = service.getMappingsForSupplier(supplierCode)
                .filter(m -> isActive == null || isActive.equals(m.getIsActive()));

        return Mono.just(ResponseEntity.ok(flux));
    }

    @Override
    public Mono<ResponseEntity<SupplierMaterialMapping>> addSupplierMaterial(
            String supplierCode,
            Mono<SupplierMaterialMapping> supplierMaterialMapping,
            ServerWebExchange exchange) {

        return supplierMaterialMapping
                .flatMap(mapping -> service.createMapping(supplierCode, mapping))
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved))
                .onErrorResume(DuplicateMappingException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                                .<SupplierMaterialMapping>build()))
                .doOnError(e -> log.error(
                        "Failed to add material mapping for supplier {}", supplierCode, e));
    }

    @Override
    public Mono<ResponseEntity<SupplierMaterialMapping>> updateSupplierMaterial(
            String supplierCode,
            String materialId,
            Mono<SupplierMaterialMapping> supplierMaterialMapping,
            ServerWebExchange exchange) {

        return supplierMaterialMapping
                .flatMap(mapping -> service.updateMapping(supplierCode, materialId, mapping))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnError(e -> log.error(
                        "Failed to update mapping supplier={} material={}", supplierCode, materialId, e));
    }

    @Override
    public Mono<ResponseEntity<Void>> removeSupplierMaterial(
            String supplierCode,
            String materialId,
            ServerWebExchange exchange) {

        return service.deleteMapping(supplierCode, materialId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnError(e -> log.error(
                        "Failed to remove mapping supplier={} material={}", supplierCode, materialId, e));
    }
}
