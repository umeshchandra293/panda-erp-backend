package com.hst.materialmgmt.controller.manufacturing;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hst.materialmgmt.entity.manufacturing.ManufacturingBatchEntity;
import com.hst.materialmgmt.entity.manufacturing.ManufacturingBomEntity;
import com.hst.materialmgmt.entity.manufacturing.ManufacturingShiftEntity;
import com.hst.materialmgmt.service.manufacturing.ManufacturingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt/manufacturing")
@Tag(name = "Manufacturing API")
public class ManufacturingController {

    @Autowired private ManufacturingService service;

    // ── Shifts ────────────────────────────────────────────────────────────────

    @GetMapping("/shifts")
    public Mono<ResponseEntity<List<ManufacturingShiftEntity>>> getAllShifts() {
        return service.getAllShifts()
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/shifts/{shiftId}")
    public Mono<ResponseEntity<ManufacturingShiftEntity>> getShiftById(
            @PathVariable String shiftId) {
        return service.getShiftById(shiftId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/shifts/{shiftId}/batches")
    public Mono<ResponseEntity<List<ManufacturingBatchEntity>>> getBatches(
            @PathVariable String shiftId) {
        return service.getBatchesByShift(shiftId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PostMapping("/shifts")
    public Mono<ResponseEntity<ManufacturingShiftEntity>> createShift(
            @RequestBody ShiftRequest req) {
        return service.createShift(req.shift(), req.batches())
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    // ── BOM ───────────────────────────────────────────────────────────────────

    @GetMapping("/bom")
    public Mono<ResponseEntity<List<ManufacturingBomEntity>>> getAllBoms() {
        return service.getAllBoms()
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/bom/{productId}")
    public Mono<ResponseEntity<List<ManufacturingBomEntity>>> getBomByProduct(
            @PathVariable String productId) {
        return service.getBomByProduct(productId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PostMapping("/bom")
    public Mono<ResponseEntity<ManufacturingBomEntity>> createBom(
            @RequestBody ManufacturingBomEntity bom) {
        return service.createBom(bom)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    @PutMapping("/bom/{bomId}")
    public Mono<ResponseEntity<ManufacturingBomEntity>> updateBom(
            @PathVariable String bomId,
            @RequestBody ManufacturingBomEntity bom) {
        return service.updateBom(bomId, bom)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/bom/{bomId}")
    public Mono<ResponseEntity<Void>> deleteBom(@PathVariable String bomId) {
        return service.deleteBom(bomId)
                .thenReturn(ResponseEntity.<Void>noContent().build());
    }

    public record ShiftRequest(
        ManufacturingShiftEntity shift,
        List<ManufacturingBatchEntity> batches
    ) {}
}