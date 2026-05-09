package com.hst.materialmgmt.controller;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.hs.api.model.GrnRequest;
import com.hs.api.model.GrnResponse;
import com.hs.api.model.RawMaterial;
import com.hst.materialmgmt.enums.MaterialCategory;
import com.hst.materialmgmt.enums.MaterialUom;
import com.hst.materialmgmt.service.RawMaterialService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@Tag(name = "Raw Material API", description = "Raw material master, GRN and lookup endpoints")
public class RawMaterialController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(RawMaterialController.class);

    private final RawMaterialService rawMaterialService;

    public RawMaterialController(RawMaterialService rawMaterialService) {
        this.rawMaterialService = rawMaterialService;
    }

    // ── Lookups ───────────────────────────────────────────────────────────────

    @GetMapping("/rawmaterial/categories")
    @Operation(summary = "Get all valid material categories")
    public Mono<ResponseEntity<List<String>>> getCategories() {
        List<String> values = Arrays.stream(MaterialCategory.values())
                .map(Enum::name).toList();
        return Mono.just(ResponseEntity.ok(values));
    }

    @GetMapping("/rawmaterial/uoms")
    @Operation(summary = "Get all valid units of measure")
    public Mono<ResponseEntity<List<String>>> getUoms() {
        List<String> values = Arrays.stream(MaterialUom.values())
                .map(Enum::name).toList();
        return Mono.just(ResponseEntity.ok(values));
    }

    // ── Raw Material CRUD ─────────────────────────────────────────────────────

    @GetMapping("/rawmaterial")
    @Operation(summary = "Get all materials")
    public Mono<ResponseEntity<Flux<RawMaterial>>> getAllMaterials(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isActive,
            ServerWebExchange exchange) {

        Flux<RawMaterial> flux = findAll(rawMaterialService, exchange)
                .cast(RawMaterial.class)
                .filter(m -> isActive == null || isActive.equals(m.getIsActive()))
                .filter(m -> category == null
                        || (m.getCategory() != null
                                && category.equalsIgnoreCase(m.getCategory().name())));

        return Mono.just(ResponseEntity.ok(flux));
    }

    @GetMapping("/rawmaterial/{materialId}")
    @Operation(summary = "Get material by ID")
    public Mono<ResponseEntity<RawMaterial>> getMaterialById(
            @PathVariable String materialId,
            ServerWebExchange exchange) {

        return findByKey(rawMaterialService, materialId, exchange)
                .cast(RawMaterial.class)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/rawmaterial")
    @Operation(summary = "Create a material")
    public Mono<ResponseEntity<RawMaterial>> createMaterial(
            @RequestBody Mono<RawMaterial> rawMaterial,
            ServerWebExchange exchange) {

        return create(rawMaterialService, rawMaterial.cast(Object.class), exchange)
                .cast(RawMaterial.class)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved))
                .doOnError(e -> log.error("Failed to create material", e));
    }

    @PutMapping("/rawmaterial/{materialId}")
    @Operation(summary = "Update a material")
    public Mono<ResponseEntity<RawMaterial>> updateMaterial(
            @PathVariable String materialId,
            @RequestBody Mono<RawMaterial> rawMaterial,
            ServerWebExchange exchange) {

        Mono<Object> genericMono = rawMaterial.map(m -> {
            m.setMaterialId(materialId);
            return (Object) m;
        });

        return update(rawMaterialService, materialId, genericMono, exchange)
                .cast(RawMaterial.class)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnError(e -> log.error("Failed to update material {}", materialId, e));
    }

    @DeleteMapping("/rawmaterial/{materialId}")
    @Operation(summary = "Delete a material")
    public Mono<ResponseEntity<Void>> deleteMaterial(
            @PathVariable String materialId,
            ServerWebExchange exchange) {
        return delete(rawMaterialService, materialId, exchange);
    }

    // ── GRN ───────────────────────────────────────────────────────────────────

    @GetMapping("/grn")
    @Operation(summary = "Get all GRNs")
    public Mono<ResponseEntity<Flux<GrnResponse>>> getAllGrns() {
        return Mono.just(ResponseEntity.ok(rawMaterialService.getAllGrns()));
    }

    @PostMapping("/grn")
    @Operation(summary = "Create a GRN")
    public Mono<ResponseEntity<GrnResponse>> createGrn(
            @RequestBody Mono<GrnRequest> grnRequest) {
        return grnRequest
                .flatMap(rawMaterialService::createGrn)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r))
                .doOnError(e -> log.error("Failed to create GRN", e));
    }

    @GetMapping("/grn/{grnId}")
    @Operation(summary = "Get GRN by ID")
    public Mono<ResponseEntity<GrnResponse>> getGrnById(
            @PathVariable String grnId) {
        return rawMaterialService.getGrnById(grnId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}