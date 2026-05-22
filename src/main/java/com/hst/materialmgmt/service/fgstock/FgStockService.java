package com.hst.materialmgmt.service.fgstock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import com.hst.materialmgmt.entity.fgstock.FgMovementEntity;
import com.hst.materialmgmt.entity.fgstock.FgStockEntity;
import com.hst.materialmgmt.repository.fgstock.FgMovementRepository;
import com.hst.materialmgmt.repository.fgstock.FgStockRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class FgStockService {

    @Autowired private FgStockRepository     stockRepo;
    @Autowired private FgMovementRepository  movementRepo;
    @Autowired private DatabaseClient        db;

    public Flux<FgStockEntity> getAllStock() {
        return db.sql("""
            SELECT f.*, p.name AS product_name, p.sku, p.uom,
                   p.units_per_box,
                   f.quantity / NULLIF(p.units_per_box, 0) AS full_boxes,
                   MOD(f.quantity, NULLIF(p.units_per_box, 1)) AS loose_units
            FROM rm_material_schema.fg_stock_tbl f
            LEFT JOIN rm_material_schema.product_tbl p ON p.product_id = f.product_id
            """)
            .map((row, meta) -> {
                FgStockEntity e = new FgStockEntity();
                e.setFgId(row.get("fg_id", String.class));
                e.setProductId(row.get("product_id", String.class));
                e.setProductName(row.get("product_name", String.class));
                e.setSku(row.get("sku", String.class));
                e.setUnit(row.get("uom", String.class));
                Integer qty = row.get("quantity", Integer.class);
                e.setQuantity(qty != null ? qty : 0);
                Integer unitsPerBox = row.get("units_per_box", Integer.class);
                e.setUnitsPerBox(unitsPerBox != null ? unitsPerBox : 1);
                Integer fullBoxes = row.get("full_boxes", Integer.class);
                e.setFullBoxes(fullBoxes != null ? fullBoxes : 0);
                Integer looseUnits = row.get("loose_units", Integer.class);
                e.setLooseUnits(looseUnits != null ? looseUnits : 0);
                return e;
            }).all();
    }

    public Flux<FgMovementEntity> getLedger(String productId, String fromDate, String toDate) {
        StringBuilder sql = new StringBuilder("""
            SELECT m.*, p.name AS product_name
            FROM rm_material_schema.fg_movement_tbl m
            LEFT JOIN rm_material_schema.product_tbl p ON p.product_id = m.product_id
            WHERE 1=1
            """);
        if (productId != null && !productId.isBlank())
            sql.append(" AND m.product_id = '").append(productId).append("'");
        if (fromDate != null && !fromDate.isBlank())
            sql.append(" AND m.movement_date >= '").append(fromDate).append("'");
        if (toDate != null && !toDate.isBlank())
            sql.append(" AND m.movement_date <= '").append(toDate).append("'");
        sql.append(" ORDER BY m.movement_date DESC");

        return db.sql(sql.toString())
            .map((row, meta) -> {
                FgMovementEntity e = new FgMovementEntity();
                e.setMovementId(row.get("movement_id", String.class));
                e.setProductId(row.get("product_id", String.class));
                e.setProductName(row.get("product_name", String.class));
                e.setMovementType(row.get("movement_type", String.class));
                Integer qty = row.get("quantity", Integer.class);
                e.setQuantity(qty != null ? qty : 0);
                e.setReferenceId(row.get("reference_id", String.class));
                e.setMovementDate(row.get("movement_date", LocalDate.class));
                e.setNotes(row.get("notes", String.class));
                return e;
            }).all();
    }

    public Mono<Void> addStock(String productId, int qty, String shiftId) {
        if (qty <= 0) return Mono.empty();

        String movementId = "FGM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        FgMovementEntity mv = new FgMovementEntity();
        mv.setMovementId(movementId);
        mv.setProductId(productId);
        mv.setMovementType("PRODUCED");
        mv.setQuantity(qty);
        mv.setReferenceId(shiftId);
        mv.setMovementDate(LocalDate.now());
        mv.setNotes("Shift production: " + shiftId);

        return movementRepo.create(mv, FgMovementEntity.class)
                .then(stockRepo.upsertStock(productId, qty));
    }

    public Mono<Void> dispatch(String productId, int qty, String referenceId, String notes) {
        return stockRepo.upsertStock(productId, -qty)
                .then(Mono.defer(() -> {
                    String movementId = "FGM-" + UUID.randomUUID().toString()
                            .substring(0, 8).toUpperCase();
                    FgMovementEntity mv = new FgMovementEntity();
                    mv.setMovementId(movementId);
                    mv.setProductId(productId);
                    mv.setMovementType("DISPATCHED");
                    mv.setQuantity(qty);
                    mv.setReferenceId(referenceId);
                    mv.setMovementDate(LocalDate.now());
                    mv.setNotes(notes);
                    return movementRepo.create(mv, FgMovementEntity.class).then();
                }));
    }
}