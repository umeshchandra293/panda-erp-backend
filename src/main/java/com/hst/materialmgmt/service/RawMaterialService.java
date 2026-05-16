package com.hst.materialmgmt.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.api.model.GrnItemRequest;
import com.hst.api.model.GrnItemResponse;
import com.hst.api.model.GrnRequest;
import com.hst.api.model.GrnResponse;
import com.hst.api.model.RawMaterial;
import com.hst.materialmgmt.entity.GrnEntity;
import com.hst.materialmgmt.entity.GrnItemEntity;
import com.hst.materialmgmt.entity.StockMovementEntity;
import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.RawMaterialMapper;
import com.hst.materialmgmt.repository.GrnItemRepository;
import com.hst.materialmgmt.repository.GrnRepository;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.RawMaterialRepository;
import com.hst.materialmgmt.repository.StockMovementRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RawMaterialService extends ParentBaseServiceImpl {

    @Autowired private RawMaterialRepository repository;
    @Autowired private RawMaterialMapper      mapper;
    @Autowired private MaterialCodeGenerator  codeGenerator;

    // ── GRN dependencies ──────────────────────────────────────────────────────
    @Autowired private GrnRepository          grnRepo;
    @Autowired private GrnItemRepository      grnItemRepo;
    @Autowired private StockMovementRepository movementRepo;

    @Override
    protected BaseMapper getMapper() { return mapper; }

    @Override
    protected ParentRepositoryImpl getParentRepository() { return repository; }

    // ── Material CRUD ─────────────────────────────────────────────────────────

    /**
     * Injects a server-generated material code before saving.
     * Clients must NOT send materialId — it is assigned here.
     */
    @Override
    public Mono<Object> createFullHierarchy(Mono<Object> parentMono) {
        if (parentMono == null)
            return Mono.error(new IllegalArgumentException("Material cannot be null"));

        Mono<Object> codedMono = parentMono.flatMap(model -> {
            RawMaterial mat = (RawMaterial) model;
            return codeGenerator.nextMaterialCode()
                    .map(code -> {
                        mat.setMaterialId(code);
                        return (Object) mat;
                    });
        });

        return super.createFullHierarchy(codedMono);
    }

    // ── GRN — list ────────────────────────────────────────────────────────────

    public Flux<GrnResponse> getAllGrns() {
        return grnRepo.findAllGrns()
                .flatMap(grn -> grnItemRepo.findByGrnId(grn.getGrnId())
                        .map(this::toItemResponse)
                        .collectList()
                        .map(items -> toGrnResponse(grn, items)));
    }

    public Mono<GrnResponse> getGrnById(String grnId) {
        return grnRepo.findByGrnId(grnId)
                .flatMap(grn -> grnItemRepo.findByGrnId(grnId)
                        .map(this::toItemResponse)
                        .collectList()
                        .map(items -> toGrnResponse(grn, items)));
    }

    // ── GRN — create ──────────────────────────────────────────────────────────
    // Flow:
    //   1. Generate GRN ID
    //   2. Save GRN header
    //   3. For each item: save GRN item + create INBOUND stock movement

    public Mono<GrnResponse> createGrn(GrnRequest req) {
        return grnRepo.nextGrnId().flatMap(grnId -> {

            GrnEntity header = new GrnEntity();
            header.setGrnId(grnId);
            header.setSupplierCode(req.getSupplierCode());
            header.setPoId(req.getPoId());
            header.setReceivedDate(req.getReceivedDate() != null
                    ? req.getReceivedDate() : LocalDate.now());
            header.setInvoiceNumber(req.getInvoiceNumber());
            header.setStatus("CONFIRMED");
            header.setNotes(req.getNotes());

            return grnRepo.create(header, GrnEntity.class)
                    .flatMap(savedHeader ->
                            Flux.fromIterable(req.getItems() != null
                                    ? req.getItems() : List.<GrnItemRequest>of())
                                    .concatMap(item -> saveItemAndMovement(grnId, item))
                                    .collectList()
                                    .map(items -> toGrnResponse(
                                            (GrnEntity) savedHeader, items)));
        });
    }

    // ── Save one GRN item + INBOUND stock movement ────────────────────────────

    private Mono<GrnItemResponse> saveItemAndMovement(
            String grnId, GrnItemRequest item) {

        return grnItemRepo.nextGrnItemId().flatMap(itemId -> {

            GrnItemEntity ie = new GrnItemEntity();
            ie.setGrnItemId(itemId);
            ie.setGrnId(grnId);
            ie.setMaterialId(item.getMaterialId());
            ie.setReceivedQty(item.getReceivedQty() != null
                    ? BigDecimal.valueOf(item.getReceivedQty()) : BigDecimal.ZERO);
            ie.setOrderedQty(item.getOrderedQty() != null
                    ? BigDecimal.valueOf(item.getOrderedQty()) : null);
            ie.setUnitCost(item.getUnitCost() != null
                    ? BigDecimal.valueOf(item.getUnitCost()) : null);
            ie.setNotes(item.getNotes());

            return grnItemRepo.create(ie, GrnItemEntity.class)
                    .cast(GrnItemEntity.class)
                    .flatMap(saved -> {
                        StockMovementEntity mv = new StockMovementEntity();
                        mv.setMovementId("MOV-" + UUID.randomUUID()
                                .toString().substring(0, 8).toUpperCase());
                        mv.setMaterialId(item.getMaterialId());
                        mv.setMovementType("INBOUND");
                        mv.setQuantity(saved.getReceivedQty());
                        mv.setUnitCost(saved.getUnitCost());
                        mv.setMovementDate(LocalDate.now());
                        mv.setReferenceType("GRN");
                        mv.setReferenceId(grnId);
                        mv.setNotes("GRN receipt: " + grnId);

                        return movementRepo.create(mv, StockMovementEntity.class)
                                .thenReturn(toItemResponse(saved));
                    });
        });
    }

    // ── Converters ────────────────────────────────────────────────────────────

    private GrnResponse toGrnResponse(GrnEntity e, List<GrnItemResponse> items) {
        GrnResponse r = new GrnResponse();
        r.setGrnId(e.getGrnId());
        r.setPoId(e.getPoId());
        r.setSupplierCode(e.getSupplierCode());
        r.setReceivedDate(e.getReceivedDate());
        r.setInvoiceNumber(e.getInvoiceNumber());
        if (e.getStatus() != null)
            r.setStatus(GrnResponse.StatusEnum.fromValue(e.getStatus()));
        r.setNotes(e.getNotes());
        r.setItems(items);
        double total = items.stream()
                .mapToDouble(i -> (i.getReceivedQty() == null
                        || i.getUnitCost() == null) ? 0.0
                        : i.getReceivedQty() * i.getUnitCost())
                .sum();
        r.setTotalValue(total);
        return r;
    }

    private GrnItemResponse toItemResponse(GrnItemEntity e) {
        GrnItemResponse r = new GrnItemResponse();
        r.setGrnItemId(e.getGrnItemId());
        r.setGrnId(e.getGrnId());
        r.setMaterialId(e.getMaterialId());
        if (e.getOrderedQty()  != null) r.setOrderedQty(e.getOrderedQty().doubleValue());
        if (e.getReceivedQty() != null) r.setReceivedQty(e.getReceivedQty().doubleValue());
        if (e.getUnitCost()    != null) r.setUnitCost(e.getUnitCost().doubleValue());
        r.setNotes(e.getNotes());
        return r;
    }
}
