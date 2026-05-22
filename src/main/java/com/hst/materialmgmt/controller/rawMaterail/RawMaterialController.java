package com.hst.materialmgmt.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import com.hst.api.RawMaterialApi;
import com.hst.api.model.RawMaterial;
import com.hst.materialmgmt.service.RawMaterialService;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "Raw Material API")
public class RawMaterialController extends BaseController implements RawMaterialApi {

    @Autowired private RawMaterialService rawMaterialService;

    // Overrides the interface's Flux version — returns collected List to avoid
    // chunked encoding issues in the browser
    @Override
    public Mono<ResponseEntity<Flux<RawMaterial>>> getAllMaterials(
            String category, Boolean isActive, ServerWebExchange exchange) {
        return findAll(rawMaterialService, exchange)
                .cast(RawMaterial.class)
                .collectList()
                .map(list -> ResponseEntity.ok(Flux.fromIterable(list)));
    }

    @Override
    public Mono<ResponseEntity<RawMaterial>> getMaterialById(
            String materialId, ServerWebExchange exchange) {
        return findByKey(rawMaterialService, materialId, exchange)
                .cast(RawMaterial.class)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<RawMaterial>> createMaterial(
            Mono<RawMaterial> rawMaterial, ServerWebExchange exchange) {
        return create(rawMaterialService, rawMaterial.cast(Object.class), exchange)
                .cast(RawMaterial.class)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    @Override
    public Mono<ResponseEntity<RawMaterial>> updateMaterial(
            String materialId, Mono<RawMaterial> rawMaterial, ServerWebExchange exchange) {
        return update(rawMaterialService, materialId, rawMaterial.cast(Object.class), exchange)
                .cast(RawMaterial.class)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteMaterial(
            String materialId, ServerWebExchange exchange) {
        return delete(rawMaterialService, materialId, exchange);
    }

    @GetMapping("/rawmaterial/categories")
    public Mono<ResponseEntity<List<String>>> getCategories() {
        return Mono.just(ResponseEntity.ok(List.of(
                "PREFORMS", "CHEMICALS", "CAPS", "LABELS", "PACKAGING", "OTHER"
        )));
    }

    @GetMapping("/rawmaterial/uoms")
    public Mono<ResponseEntity<List<String>>> getUoms() {
        return Mono.just(ResponseEntity.ok(List.of(
                "KG", "GM", "LTR", "ML", "PCS", "ROLL", "BAG", "BOX"
        )));
    }
}