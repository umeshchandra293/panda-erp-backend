package com.hst.materialmgmt.service.manufacturing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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

    // ── Create shift ──────────────────────────────────────────────────────────

    public Mono<ManufacturingShiftEntity> createShift(
            ManufacturingShiftEntity shift,
            List<ManufacturingBatchEntity> batches) {

        int totalUnits    = batches.stream().mapToInt(b -> b.getActualQty()   != null ? b.getActualQty()   : 0).sum();
        int totalRejected = batches.stream().mapToInt(b -> b.getRejectedQty() != null ? b.getRejectedQty() : 0).sum();
        shift.setTotalUnits(totalUnits);
        shift.setTotalRejected(totalRejected);
        shift.setStatus("CONFIRMED");

        return validateAllMaterialsHaveStock(batches)
                .then(shiftRepo.nextShiftId())
                .flatMap(shiftId -> {
                    shift.setShiftId(shiftId);
                    return shiftRepo.create(shift, ManufacturingShiftEntity.class)
                            .flatMap(saved ->
                                Flux.fromIterable(batches)
                                    .concatMap(batch -> saveBatchAndDeductStock(shiftId, batch))
                                    .then(Mono.just((ManufacturingShiftEntity) saved)));
                });
    }

    // ── Update shift header ───────────────────────────────────────────────────
    // Updates date, operatorName, shiftType, notes only.
    // Does NOT adjust stock movements — quantities are locked after creation.

    public Mono<ManufacturingShiftEntity> updateShift(String shiftId,
                                                       ManufacturingShiftEntity updates) {
        return shiftRepo.findById(shiftId)
                .cast(ManufacturingShiftEntity.class)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Shift not found: " + shiftId)))
                .flatMap(existing -> {
                    existing.setShiftDate(updates.getShiftDate() != null
                            ? updates.getShiftDate() : existing.getShiftDate());
                    existing.setOperatorName(updates.getOperatorName() != null
                            ? updates.getOperatorName() : existing.getOperatorName());
                    existing.setShiftType(updates.getShiftType() != null
                            ? updates.getShiftType() : existing.getShiftType());
                    existing.setNotes(updates.getNotes() != null
                            ? updates.getNotes() : existing.getNotes());
                    return shiftRepo.update(shiftId, existing)
                            .cast(ManufacturingShiftEntity.class);
                });
    }

    // ── Update batch quantities ───────────────────────────────────────────────
    // Updates actualQty and rejectedQty, recalculates shift totals.
    // Note: stock movements are NOT reversed/adjusted — use inventory adjustment
    // on the Inventory page if stock correction is needed.

    public Mono<ManufacturingBatchEntity> updateBatch(String batchId,
                                                       int actualQty, int rejectedQty) {
        return batchRepo.findByBatchId(batchId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Batch not found: " + batchId)))
                .flatMap(batch -> {
                    batch.setActualQty(actualQty);
                    batch.setRejectedQty(rejectedQty);
                    return batchRepo.update(batchId, batch)
                            .cast(ManufacturingBatchEntity.class)
                            .flatMap(updated ->
                                // Recalculate shift totals
                                batchRepo.findByShiftId(batch.getShiftId())
                                    .collectList()
                                    .flatMap(allBatches -> {
                                        int newTotal    = allBatches.stream().mapToInt(b -> b.getActualQty()   != null ? b.getActualQty()   : 0).sum();
                                        int newRejected = allBatches.stream().mapToInt(b -> b.getRejectedQty() != null ? b.getRejectedQty() : 0).sum();
                                        return shiftRepo.findById(batch.getShiftId())
                                                .cast(ManufacturingShiftEntity.class)
                                                .flatMap(shift -> {
                                                    shift.setTotalUnits(newTotal);
                                                    shift.setTotalRejected(newRejected);
                                                    return shiftRepo.update(batch.getShiftId(), shift);
                                                });
                                    })
                                    .thenReturn(updated));
                });
    }

    // ── Delete shift ──────────────────────────────────────────────────────────
    // Deletes:  shift header + all batches + RM consumption movements
    // Reverses: FG stock quantities produced by this shift
    // Does NOT reverse: FG dispatch records already made from this stock

    public Mono<Void> deleteShift(String shiftId) {
        return shiftRepo.findById(shiftId)
                .cast(ManufacturingShiftEntity.class)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Shift not found: " + shiftId)))
                .flatMap(shift ->
                    // 1. Reverse FG stock for each batch
                    batchRepo.findByShiftId(shiftId)
                        .flatMap(batch -> {
                            int qty = batch.getActualQty() != null ? batch.getActualQty() : 0;
                            if (qty <= 0) return Mono.empty();
                            // Subtract from FG stock (negative = reduction)
                            return fgStockService.addStock(batch.getProductId(), -qty, shiftId);
                        })
                        .then()
                        // 2. Delete RM consumption movements for this shift
                        .then(movementRepo.deleteByReferenceId(shiftId))
                        // 3. Delete batch records
                        .then(batchRepo.deleteByShiftId(shiftId))
                        // 4. Delete shift header
                        .then(shiftRepo.deleteById(shiftId))
                        .then()
                );
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private Mono<Void> validateAllMaterialsHaveStock(List<ManufacturingBatchEntity> batches) {
        return Flux.fromIterable(batches)
                .concatMap(batch -> {
                    int qty = batch.getActualQty() != null ? batch.getActualQty() : 0;
                    if (qty <= 0) return Flux.empty();
                    return bomRepo.findByProductId(batch.getProductId())
                            .flatMap(bom -> {
                                BigDecimal required = bom.getQtyPerUnit()
                                        .multiply(BigDecimal.valueOf(qty));
                                return getCurrentStock(bom.getMaterialId())
                                        .flatMap(currentStock -> {
                                            if (required.compareTo(currentStock) > 0) {
                                                return Mono.error(new ResponseStatusException(
                                                    HttpStatus.BAD_REQUEST,
                                                    String.format(
                                                        "Insufficient stock for material %s — " +
                                                        "required: %.4f, available: %.4f. " +
                                                        "Please receive more stock before saving this shift.",
                                                        bom.getMaterialId(),
                                                        required.doubleValue(),
                                                        currentStock.doubleValue())));
                                            }
                                            return Mono.empty();
                                        });
                            });
                }).then();
    }

    private Mono<BigDecimal> getCurrentStock(String materialId) {
        return movementRepo.findFiltered(materialId, null, null, null)
                .reduce(BigDecimal.ZERO, (stock, movement) -> {
                    BigDecimal qty = movement.getQuantity() != null
                            ? movement.getQuantity() : BigDecimal.ZERO;
                    String type = movement.getMovementType() != null
                            ? movement.getMovementType().toUpperCase() : "";
                    return switch (type) {
                        case "INBOUND", "ADJUSTMENT" -> stock.add(qty);
                        case "CONSUMPTION", "WASTAGE" -> stock.subtract(qty);
                        default -> stock;
                    };
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
                                .then(fgStockService.addStock(
                                        batch.getProductId(), actualQty, shiftId));
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