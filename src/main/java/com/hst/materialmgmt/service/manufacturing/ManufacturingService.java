package com.hst.materialmgmt.service.manufacturing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hst.materialmgmt.entity.StockMovementEntity;
import com.hst.materialmgmt.entity.manufacturing.ManufacturingBatchEntity;
import com.hst.materialmgmt.entity.manufacturing.ManufacturingBomEntity;
import com.hst.materialmgmt.entity.manufacturing.ManufacturingShiftEntity;
import com.hst.materialmgmt.repository.StockMovementRepository;
import com.hst.materialmgmt.repository.manufacturing.ManufacturingBatchRepository;
import com.hst.materialmgmt.repository.manufacturing.ManufacturingBomRepository;
import com.hst.materialmgmt.repository.manufacturing.ManufacturingShiftRepository;
import com.hst.materialmgmt.service.fgstock.FgStockService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ManufacturingService {

    @Autowired private ManufacturingShiftRepository shiftRepo;
    @Autowired private ManufacturingBatchRepository batchRepo;
    @Autowired private ManufacturingBomRepository   bomRepo;
    @Autowired private StockMovementRepository      movementRepo;
    @Autowired private FgStockService               fgStockService;

    // ── Shift CRUD ────────────────────────────────────────────────────────────

    public Flux<ManufacturingShiftEntity> getAllShifts() {
        return shiftRepo.findAllShifts();
    }

    public Mono<ManufacturingShiftEntity> getShiftById(String shiftId) {
        return shiftRepo.findById(shiftId).cast(ManufacturingShiftEntity.class);
    }

    public Flux<ManufacturingBatchEntity> getBatchesByShift(String shiftId) {
        return batchRepo.findByShiftId(shiftId);
    }

    /**
     * Create shift:
     * 1. Generate shift ID
     * 2. Save shift header
     * 3. For each batch: save batch + deduct raw materials via BOM
     */
    public Mono<ManufacturingShiftEntity> createShift(
            ManufacturingShiftEntity shift,
            List<ManufacturingBatchEntity> batches) {

        int totalUnits    = batches.stream().mapToInt(b -> b.getActualQty()   != null ? b.getActualQty()   : 0).sum();
        int totalRejected = batches.stream().mapToInt(b -> b.getRejectedQty() != null ? b.getRejectedQty() : 0).sum();
        shift.setTotalUnits(totalUnits);
        shift.setTotalRejected(totalRejected);
        shift.setStatus("CONFIRMED");

        return shiftRepo.nextShiftId().flatMap(shiftId -> {
            shift.setShiftId(shiftId);
            return shiftRepo.create(shift, ManufacturingShiftEntity.class)
                    .flatMap(saved ->
                        Flux.fromIterable(batches)
                            .concatMap(batch -> saveBatchAndDeductStock(shiftId, batch))
                            .then(Mono.just((ManufacturingShiftEntity) saved)));
        });
    }

    private Mono<Void> saveBatchAndDeductStock(String shiftId, ManufacturingBatchEntity batch) {
        return batchRepo.nextBatchId().flatMap(batchId -> {
            batch.setBatchId(batchId);
            batch.setShiftId(shiftId);
            return batchRepo.create(batch, ManufacturingBatchEntity.class)
                    .flatMap(saved -> {
                        int actualQty = batch.getActualQty() != null ? batch.getActualQty() : 0;
                        return deductRawMaterials(shiftId, batch.getProductId(), actualQty)
                                .then(fgStockService.addStock(batch.getProductId(), actualQty, shiftId));
                    });
        });
    }

    private Mono<Void> deductRawMaterials(String shiftId, String productId, int actualQty) {
        if (actualQty <= 0) return Mono.empty();
        return bomRepo.findByProductId(productId)
                .concatMap(bom -> {
                    BigDecimal consumed = bom.getQtyPerUnit()
                            .multiply(BigDecimal.valueOf(actualQty));
                    StockMovementEntity mv = new StockMovementEntity();
                    mv.setMovementId("MOV-" + UUID.randomUUID().toString()
                            .substring(0, 8).toUpperCase());
                    mv.setMaterialId(bom.getMaterialId());
                    mv.setMovementType("CONSUMPTION");
                    mv.setQuantity(consumed);
                    mv.setMovementDate(LocalDate.now());
                    mv.setReferenceType("PRODUCTION");
                    mv.setReferenceId(shiftId);
                    mv.setNotes("Manufacturing shift: " + shiftId);
                    return movementRepo.create(mv, StockMovementEntity.class);
                }).then();
    }

    // ── BOM CRUD ──────────────────────────────────────────────────────────────

    public Flux<ManufacturingBomEntity> getAllBoms() {
        return bomRepo.findAllBoms();
    }

    public Flux<ManufacturingBomEntity> getBomByProduct(String productId) {
        return bomRepo.findByProductId(productId);
    }

    public Mono<ManufacturingBomEntity> createBom(ManufacturingBomEntity bom) {
        bom.setIsActive(true);
        return bomRepo.nextBomId().flatMap(id -> {
            bom.setBomId(id);
            return bomRepo.create(bom, ManufacturingBomEntity.class)
                    .cast(ManufacturingBomEntity.class);
        });
    }

    public Mono<ManufacturingBomEntity> updateBom(String bomId, ManufacturingBomEntity bom) {
        bom.setBomId(bomId);
        return bomRepo.update(bomId, bom).cast(ManufacturingBomEntity.class);
    }

    public Mono<Void> deleteBom(String bomId) {
        return bomRepo.deleteById(bomId).then();
    }
}
