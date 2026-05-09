package com.hst.materialmgmt.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hs.api.model.Retailer;
import com.hs.api.model.RetailerLedger;
import com.hs.api.model.Salesman;
import com.hs.api.model.SalesKpis;
import com.hs.api.model.SalesOrder;
import com.hs.api.model.SalesOrderItem;
import com.hs.api.model.SalesPayment;
import com.hs.api.model.SalesProduct;
import com.hs.api.model.SalesVisit;
import com.hs.api.model.SalesmanActivityRow;
import com.hs.api.model.SalesmanDaySummary;
import com.hst.materialmgmt.entity.RetailerEntity;
import com.hst.materialmgmt.entity.SalesmanEntity;
import com.hst.materialmgmt.entity.SalesOrderEntity;
import com.hst.materialmgmt.entity.SalesOrderItemEntity;
import com.hst.materialmgmt.entity.SalesPaymentEntity;
import com.hst.materialmgmt.entity.SalesVisitEntity;
import com.hst.materialmgmt.repository.RetailerRepository;
import com.hst.materialmgmt.repository.SalesOrderItemRepository;
import com.hst.materialmgmt.repository.SalesOrderRepository;
import com.hst.materialmgmt.repository.SalesPaymentRepository;
import com.hst.materialmgmt.repository.SalesProductRepository;
import com.hst.materialmgmt.repository.SalesVisitRepository;
import com.hst.materialmgmt.repository.SalesmanRepository;
import com.hst.materialmgmt.service.SalesKpisRow;
import com.hst.materialmgmt.service.SalesmanActivityRowData;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SalesService {

    @Autowired private RetailerRepository retailerRepo;
    @Autowired private SalesmanRepository salesmanRepo;
    @Autowired private SalesProductRepository productRepo;
    @Autowired private SalesVisitRepository visitRepo;
    @Autowired private SalesOrderRepository orderRepo;
    @Autowired private SalesOrderItemRepository itemRepo;
    @Autowired private SalesPaymentRepository paymentRepo;
    @Autowired private SalesCodeGenerator codeGen;

    // ── Retailers ─────────────────────────────────────────────────────

    public Flux<Retailer> getRetailers(String salesmanId, String area, Boolean isActive) {
        return retailerRepo.findFilteredWithSalesmanName(salesmanId, area, isActive)
                .map(row -> {
                    Retailer r = new Retailer();
                    r.setRetailerId((String) row[0]);
                    r.setShopName((String) row[1]);
                    r.setOwnerName((String) row[2]);
                    r.setPhone((String) row[3]);
                    r.setAddress((String) row[4]);
                    r.setArea((String) row[5]);
                    if (row[6] != null) r.setGpsLat(((java.math.BigDecimal) row[6]).doubleValue());
                    if (row[7] != null) r.setGpsLng(((java.math.BigDecimal) row[7]).doubleValue());
                    r.setAssignedSalesmanId((String) row[8]);
                    if (row[9]  != null) r.setCreditLimit(((java.math.BigDecimal) row[9]).doubleValue());
                    if (row[10] != null) r.setCurrentBalance(((java.math.BigDecimal) row[10]).doubleValue());
                    r.setIsActive((Boolean) row[11]);
                    r.setAssignedSalesmanName((String) row[12]);
                    return r;
                });
    }

    public Mono<Retailer> getRetailerById(String id) {
        return retailerRepo.findRetailerById(id)
                .map(this::toRetailerModel);
    }

    public Mono<Retailer> createRetailer(Retailer req) {
        return codeGen.nextRetailerId().flatMap(id -> {
            RetailerEntity e = new RetailerEntity();
            e.setRetailerId(id);
            e.setShopName(req.getShopName());
            e.setOwnerName(req.getOwnerName());
            e.setPhone(req.getPhone());
            e.setAddress(req.getAddress());
            e.setArea(req.getArea());
            if (req.getGpsLat() != null) e.setGpsLat(BigDecimal.valueOf(req.getGpsLat()));
            if (req.getGpsLng() != null) e.setGpsLng(BigDecimal.valueOf(req.getGpsLng()));
            e.setAssignedSalesmanId(req.getAssignedSalesmanId());
            e.setCreditLimit(req.getCreditLimit() != null
                    ? BigDecimal.valueOf(req.getCreditLimit()) : new BigDecimal("5000"));
            e.setCurrentBalance(BigDecimal.ZERO);
            e.setIsActive(Boolean.TRUE);
            return retailerRepo.create(e, RetailerEntity.class)
                    .cast(RetailerEntity.class)
                    .map(this::toRetailerModel);
        });
    }

    public Mono<Retailer> updateRetailer(String id, Retailer req) {
        return retailerRepo.findRetailerById(id).flatMap(e -> {
            if (req.getShopName() != null)  e.setShopName(req.getShopName());
            if (req.getOwnerName() != null) e.setOwnerName(req.getOwnerName());
            if (req.getPhone() != null)     e.setPhone(req.getPhone());
            if (req.getAddress() != null)   e.setAddress(req.getAddress());
            if (req.getArea() != null)      e.setArea(req.getArea());
            if (req.getCreditLimit() != null)
                e.setCreditLimit(BigDecimal.valueOf(req.getCreditLimit()));
            if (req.getIsActive() != null)  e.setIsActive(req.getIsActive());
            return retailerRepo.update(id, e)
                    .then(retailerRepo.findRetailerById(id))
                    .map(this::toRetailerModel);
        });
    }

    public Mono<RetailerLedger> getRetailerLedger(String retailerId) {
        Mono<Retailer> retailerMono = getRetailerById(retailerId);
        Mono<List<SalesOrder>> ordersMono =
                getOrders(null, retailerId, null, null).collectList();
        Mono<List<SalesPayment>> paymentsMono =
                getPayments(null, retailerId, null, null).collectList();
        return Mono.zip(retailerMono, ordersMono, paymentsMono).map(t -> {
            RetailerLedger ledger = new RetailerLedger();
            ledger.setRetailer(t.getT1());
            ledger.setOrders(t.getT2());
            ledger.setPayments(t.getT3());
            return ledger;
        });
    }

    // ── Products ───────────────────────────────────────────────────────

    public Flux<SalesProduct> getAllProducts() {
        return productRepo.findAll()
                .map(raw -> {
                    var pe = (com.hst.materialmgmt.entity.SalesProductEntity) raw;
                    SalesProduct p = new SalesProduct();
                    p.setProductId(pe.getProductId());
                    p.setProductName(pe.getProductName());
                    p.setSku(pe.getSku());
                    p.setBasePrice(pe.getBasePrice() != null ? pe.getBasePrice().doubleValue() : 0.0);
                    p.setEffectivePrice(p.getBasePrice());
                    p.setUnit(pe.getUnit());
                    p.setIsActive(pe.getIsActive());
                    return p;
                });
    }

    public Flux<SalesProduct> getProductsForRetailer(String retailerId) {
        return productRepo.findProductsWithPricingForRetailer(retailerId)
                .map(row -> {
                    SalesProduct p = new SalesProduct();
                    p.setProductId((String) row[0]);
                    p.setProductName((String) row[1]);
                    p.setSku((String) row[2]);
                    p.setBasePrice(row[3] != null ? ((BigDecimal) row[3]).doubleValue() : 0.0);
                    p.setEffectivePrice(row[4] != null ? ((BigDecimal) row[4]).doubleValue() : 0.0);
                    p.setUnit((String) row[5]);
                    p.setIsActive((Boolean) row[6]);
                    return p;
                });
    }

    // ── Visits ─────────────────────────────────────────────────────────

    public Flux<SalesVisit> getVisits(
            String salesmanId, String retailerId, LocalDate from, LocalDate to) {
        return visitRepo.findFiltered(salesmanId, retailerId, from, to)
                .map(this::toVisitModel);
    }

    public Mono<SalesVisit> createVisit(SalesVisit req) {
        SalesVisitEntity e = new SalesVisitEntity();
        e.setSalesmanId(req.getSalesmanId());
        e.setRetailerId(req.getRetailerId());
        e.setVisitDate(req.getVisitDate() != null ? req.getVisitDate() : LocalDate.now());
        e.setCheckInTime(LocalDateTime.now());
        if (req.getGpsLat() != null) e.setGpsLat(BigDecimal.valueOf(req.getGpsLat()));
        if (req.getGpsLng() != null) e.setGpsLng(BigDecimal.valueOf(req.getGpsLng()));
        e.setGpsVerified(req.getGpsVerified() != null ? req.getGpsVerified() : Boolean.FALSE);
        e.setRemarks(req.getRemarks());
        e.setStatus(req.getStatus() != null ? req.getStatus().name() : "VISITED");

        return visitRepo.nextVisitId()
                .flatMap(visitId -> {
                    e.setVisitId(visitId);
                    return visitRepo.create(e, SalesVisitEntity.class)
                            .cast(SalesVisitEntity.class)
                            .map(this::toVisitModel);
                });
    }

    // ── Orders ─────────────────────────────────────────────────────────

    public Flux<SalesOrder> getOrders(
            String salesmanId, String retailerId, LocalDate from, LocalDate to) {
        return orderRepo.findFiltered(salesmanId, retailerId, from, to)
                .flatMap(e -> itemRepo.findByOrderId(e.getOrderId())
                        .map(this::toOrderItemModel)
                        .collectList()
                        .map(items -> toOrderModel(e, items)));
    }

    public Mono<SalesOrder> createOrder(SalesOrder req) {
        return retailerRepo.findRetailerById(req.getRetailerId())
                .flatMap(retailer -> {
                    BigDecimal orderTotal = req.getItems() == null ? BigDecimal.ZERO
                            : req.getItems().stream()
                                    .map(i -> BigDecimal.valueOf(
                                            i.getLineTotal() != null ? i.getLineTotal() : 0))
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal newBalance = retailer.getCurrentBalance().add(orderTotal);
                    if (newBalance.compareTo(retailer.getCreditLimit()) > 0) {
                        return Mono.error(new RuntimeException(
                                "CREDIT_LIMIT_EXCEEDED: Balance would be ₹" + newBalance +
                                " against limit of ₹" + retailer.getCreditLimit()));
                    }

                    return codeGen.nextOrderId().flatMap(orderId -> {
                        SalesOrderEntity e = new SalesOrderEntity();
                        e.setOrderId(orderId);
                        e.setSalesmanId(req.getSalesmanId());
                        e.setRetailerId(req.getRetailerId());
                        e.setVisitId(req.getVisitId());
                        e.setOrderDate(req.getOrderDate() != null
                                ? req.getOrderDate() : LocalDate.now());
                        e.setTotalAmount(orderTotal);
                        e.setStatus("PLACED");
                        e.setNotes(req.getNotes());

                        return orderRepo.create(e, SalesOrderEntity.class)
                                .flatMap(savedOrder -> {
                                    List<SalesOrderItem> items = req.getItems() != null
                                            ? req.getItems() : List.of();

                                    return Flux.fromIterable(items)
                                            .flatMap(item -> codeGen.nextOrderItemId()
                                                    .flatMap(itemId -> {
                                                        SalesOrderItemEntity ie =
                                                                new SalesOrderItemEntity();
                                                        ie.setItemId(itemId);
                                                        ie.setOrderId(orderId);
                                                        ie.setProductId(item.getProductId());
                                                        ie.setQuantity(BigDecimal.valueOf(
                                                                item.getQuantity()));
                                                        ie.setUnitPrice(BigDecimal.valueOf(
                                                                item.getUnitPrice()));
                                                        ie.setLineTotal(BigDecimal.valueOf(
                                                                item.getLineTotal()));
                                                        return itemRepo.create(
                                                                ie, SalesOrderItemEntity.class)
                                                                .cast(SalesOrderItemEntity.class)
                                                                .map(this::toOrderItemModel);
                                                    }))
                                            .collectList()
                                            .flatMap(savedItems ->
                                                    retailerRepo.updateBalance(
                                                            req.getRetailerId(), newBalance)
                                                            .thenReturn(toOrderModel(
                                                                    (SalesOrderEntity) savedOrder,
                                                                    savedItems)));
                                });
                    });
                });
    }

    public Mono<SalesOrder> updateOrderStatus(String orderId, String status) {
        return orderRepo.updateStatus(orderId, status)
                .then(orderRepo.findFiltered(null, null, null, null)
                        .filter(e -> e.getOrderId().equals(orderId)).next())
                .flatMap(e -> itemRepo.findByOrderId(orderId)
                        .map(this::toOrderItemModel).collectList()
                        .map(items -> toOrderModel(e, items)));
    }

    // ── Payments ───────────────────────────────────────────────────────

    public Flux<SalesPayment> getPayments(
            String salesmanId, String retailerId, LocalDate from, LocalDate to) {
        return paymentRepo.findFiltered(salesmanId, retailerId, from, to)
                .map(this::toPaymentModel);
    }

    public Mono<SalesPayment> createPayment(SalesPayment req) {
        return retailerRepo.findRetailerById(req.getRetailerId())
                .flatMap(retailer -> codeGen.nextPaymentId().flatMap(paymentId -> {
                    SalesPaymentEntity e = new SalesPaymentEntity();
                    e.setPaymentId(paymentId);
                    e.setSalesmanId(req.getSalesmanId());
                    e.setRetailerId(req.getRetailerId());
                    e.setVisitId(req.getVisitId());
                    e.setPaymentDate(req.getPaymentDate() != null
                            ? req.getPaymentDate() : LocalDate.now());
                    e.setAmount(BigDecimal.valueOf(req.getAmount()));
                    e.setPaymentMode(req.getPaymentMode() != null
                            ? req.getPaymentMode().name() : "CASH");
                    e.setReferenceNumber(req.getReferenceNumber());
                    e.setNotes(req.getNotes());

                    BigDecimal newBalance = retailer.getCurrentBalance()
                            .subtract(BigDecimal.valueOf(req.getAmount()))
                            .max(BigDecimal.ZERO);

                    return paymentRepo.create(e, SalesPaymentEntity.class)
                            .cast(SalesPaymentEntity.class)
                            .flatMap(saved -> retailerRepo.updateBalance(
                                    req.getRetailerId(), newBalance)
                                    .thenReturn(toPaymentModel(saved)));
                }));
    }

    // ── Salesman ───────────────────────────────────────────────────────

    public Mono<Salesman> getSalesmanByUsername(String username) {
        return salesmanRepo.findByUsername(username).map(this::toSalesmanModel);
    }

    public Mono<SalesmanDaySummary> getSalesmanToday(String salesmanId) {
        return salesmanRepo.findAll()
                .cast(SalesmanEntity.class)
                .filter(s -> s.getSalesmanId().equals(salesmanId)
                          || s.getUsername().equals(salesmanId))
                .next()
                .flatMap(salesman -> {
                    String sid = salesman.getSalesmanId();
                    Mono<Long>        visitsMono    = visitRepo.countTodayBySalesman(sid);
                    Mono<Long>        ordersMono    = orderRepo.countTodayBySalesman(sid);
                    Mono<BigDecimal>  colMono       = paymentRepo.sumTodayBySalesman(sid);
                    Mono<List<Retailer>> retailersMono =
                            retailerRepo.findBySalesmanId(sid)
                                    .map(this::toRetailerModel).collectList();
                    Mono<long[]>      targetMono    = salesmanRepo.findTodayTarget(sid);

                    return Mono.zip(visitsMono, ordersMono, colMono, retailersMono, targetMono)
                            .map(t -> {
                                long[] targets = t.getT5();
                                SalesmanDaySummary s = new SalesmanDaySummary();
                                s.setSalesman(toSalesmanModel(salesman));
                                s.setVisitsToday(t.getT1().intValue());
                                s.setOrdersToday(t.getT2().intValue());
                                s.setCollectionsToday(t.getT3().doubleValue());
                                s.setRetailers(t.getT4());
                                s.setVisitTarget((int) targets[0]);
                                s.setOrderTarget((int) targets[1]);
                                s.setCollectionTarget(0.0);
                                return s;
                            });
                });
    }

    // ── Dashboard ──────────────────────────────────────────────────────

    public Mono<SalesKpis> getKpis() {
        return retailerRepo.findKpiTotals().map(row -> {
                    SalesKpis k = new SalesKpis();
                    k.setTotalRetailers(safeInt(row.totalRetailers()));
                    k.setActiveRetailers(safeInt(row.activeRetailers()));
                    k.setTotalReceivables(safeDbl(row.totalReceivables()));
                    k.setOutstandingShops(safeInt(row.outstandingShops()));
                    k.setCriticalAccounts(safeInt(row.criticalAccounts()));
                    k.setTodayCollections(0.0);
                    k.setTodayOrders(0);
                    return k;
                });
    }

    public Flux<SalesmanActivityRow> getSalesActivity(LocalDate date) {
        LocalDate d = date != null ? date : LocalDate.now();
        return salesmanRepo.findActivityForDate(d)
                .map(row -> {
                    SalesmanActivityRow ar = new SalesmanActivityRow();
                    ar.setSalesmanId(row.salesmanId());
                    ar.setSalesmanName(row.salesmanName());
                    ar.setRouteName(row.routeName());
                    ar.setVisitTarget(safeInt(row.visitTarget()));
                    ar.setVisitsToday(safeInt(row.visitsToday()));
                    ar.setOrdersToday(safeInt(row.ordersToday()));
                    ar.setCollectionsToday(safeDbl(row.collectionsToday()));
                    int visits = ar.getVisitsToday();
                    int orders = ar.getOrdersToday();
                    ar.setConversionRate(visits > 0
                            ? Math.round((orders * 100.0) / visits) : 0.0);
                    return ar;
                });
    }

    // ── Model converters ───────────────────────────────────────────────

    private Retailer toRetailerModel(RetailerEntity e) {
        Retailer r = new Retailer();
        r.setRetailerId(e.getRetailerId());
        r.setShopName(e.getShopName());
        r.setOwnerName(e.getOwnerName());
        r.setPhone(e.getPhone());
        r.setAddress(e.getAddress());
        r.setArea(e.getArea());
        if (e.getGpsLat() != null)       r.setGpsLat(e.getGpsLat().doubleValue());
        if (e.getGpsLng() != null)       r.setGpsLng(e.getGpsLng().doubleValue());
        r.setAssignedSalesmanId(e.getAssignedSalesmanId());
        if (e.getCreditLimit() != null)  r.setCreditLimit(e.getCreditLimit().doubleValue());
        if (e.getCurrentBalance() != null) r.setCurrentBalance(e.getCurrentBalance().doubleValue());
        r.setIsActive(e.getIsActive());
        return r;
    }

    private Salesman toSalesmanModel(SalesmanEntity e) {
        Salesman s = new Salesman();
        s.setSalesmanId(e.getSalesmanId());
        s.setUsername(e.getUsername());
        s.setFullName(e.getFullName());
        s.setPhone(e.getPhone());
        s.setRouteId(e.getRouteId());
        s.setIsActive(e.getIsActive());
        return s;
    }

    private SalesVisit toVisitModel(SalesVisitEntity e) {
        SalesVisit v = new SalesVisit();
        v.setVisitId(e.getVisitId());
        v.setSalesmanId(e.getSalesmanId());
        v.setRetailerId(e.getRetailerId());
        v.setVisitDate(e.getVisitDate());
        if (e.getCheckInTime() != null)
            v.setCheckInTime(e.getCheckInTime()
                    .atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime());
        if (e.getGpsLat() != null) v.setGpsLat(e.getGpsLat().doubleValue());
        if (e.getGpsLng() != null) v.setGpsLng(e.getGpsLng().doubleValue());
        v.setGpsVerified(e.getGpsVerified());
        v.setRemarks(e.getRemarks());
        if (e.getStatus() != null)
            v.setStatus(SalesVisit.StatusEnum.fromValue(e.getStatus()));
        return v;
    }

    private SalesOrder toOrderModel(SalesOrderEntity e, List<SalesOrderItem> items) {
        SalesOrder o = new SalesOrder();
        o.setOrderId(e.getOrderId());
        o.setSalesmanId(e.getSalesmanId());
        o.setRetailerId(e.getRetailerId());
        o.setVisitId(e.getVisitId());
        o.setOrderDate(e.getOrderDate());
        if (e.getTotalAmount() != null) o.setTotalAmount(e.getTotalAmount().doubleValue());
        if (e.getStatus() != null)
            o.setStatus(SalesOrder.StatusEnum.fromValue(e.getStatus()));
        o.setNotes(e.getNotes());
        o.setItems(items);
        return o;
    }

    private SalesOrderItem toOrderItemModel(SalesOrderItemEntity e) {
        SalesOrderItem i = new SalesOrderItem();
        i.setItemId(e.getItemId());
        i.setProductId(e.getProductId());
        if (e.getQuantity() != null)  i.setQuantity(e.getQuantity().doubleValue());
        if (e.getUnitPrice() != null) i.setUnitPrice(e.getUnitPrice().doubleValue());
        if (e.getLineTotal() != null) i.setLineTotal(e.getLineTotal().doubleValue());
        return i;
    }

    private SalesPayment toPaymentModel(SalesPaymentEntity e) {
        SalesPayment p = new SalesPayment();
        p.setPaymentId(e.getPaymentId());
        p.setSalesmanId(e.getSalesmanId());
        p.setRetailerId(e.getRetailerId());
        p.setVisitId(e.getVisitId());
        p.setPaymentDate(e.getPaymentDate());
        if (e.getAmount() != null) p.setAmount(e.getAmount().doubleValue());
        if (e.getPaymentMode() != null)
            p.setPaymentMode(SalesPayment.PaymentModeEnum.fromValue(e.getPaymentMode()));
        p.setReferenceNumber(e.getReferenceNumber());
        p.setNotes(e.getNotes());
        return p;
    }

    private static int    safeInt(Long v)       { return v == null ? 0 : v.intValue(); }
    private static double safeDbl(BigDecimal v) { return v == null ? 0.0 : v.doubleValue(); }
}