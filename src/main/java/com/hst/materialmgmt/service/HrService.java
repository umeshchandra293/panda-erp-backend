package com.hst.materialmgmt.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.api.model.DailyTarget;
import com.hst.api.model.SalesmanProfile;
import com.hst.api.model.SalesRoute;
import com.hst.materialmgmt.entity.DailyTargetEntity;
import com.hst.materialmgmt.entity.SalesRouteEntity;
import com.hst.materialmgmt.entity.SalesmanEntity;
import com.hst.materialmgmt.repository.DailyTargetRepository;
import com.hst.materialmgmt.repository.SalesRouteRepository;
import com.hst.materialmgmt.repository.SalesmanRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HrService {

    @Autowired private SalesmanRepository salesmanRepo;
    @Autowired private SalesRouteRepository routeRepo;
    @Autowired private DailyTargetRepository targetRepo;
    @Autowired private SalesCodeGenerator codeGen;

    // ── Salesmen ───────────────────────────────────────────────────────

    public Flux<SalesmanProfile> getAllSalesmen(Boolean isActive) {
        return salesmanRepo.findAll()
                .cast(SalesmanEntity.class)
                .filter(e -> isActive == null || isActive.equals(e.getIsActive()))
                .flatMap(e -> routeRepo.findById(e.getRouteId() != null ? e.getRouteId() : "")
                        .cast(SalesRouteEntity.class)
                        .map(route -> toProfile(e, route))
                        .defaultIfEmpty(toProfile(e, null)));
    }

    public Mono<SalesmanProfile> getSalesmanById(String salesmanId) {
        return salesmanRepo.findAll()
                .cast(SalesmanEntity.class)
                .filter(e -> e.getSalesmanId().equals(salesmanId))
                .next()
                .flatMap(e -> e.getRouteId() != null
                        ? routeRepo.findById(e.getRouteId())
                                .cast(SalesRouteEntity.class)
                                .map(r -> toProfile(e, r))
                                .defaultIfEmpty(toProfile(e, null))
                        : Mono.just(toProfile(e, null)));
    }

    public Mono<SalesmanProfile> createSalesman(SalesmanProfile req) {
        // Generate next salesman sequence
        return salesmanRepo.nextSalesmanId()
                .flatMap(salesmanId -> {
                    SalesmanEntity e = new SalesmanEntity();
                    e.setSalesmanId(salesmanId);
                    e.setUsername(req.getUsername());
                    e.setFullName(req.getFullName());
                    e.setPhone(req.getPhone());
                    e.setRouteId(req.getRouteId());
                    e.setIsActive(Boolean.TRUE);
                    return salesmanRepo.create(e, SalesmanEntity.class)
                            .cast(SalesmanEntity.class)
                            .flatMap(saved -> saved.getRouteId() != null
                                    ? routeRepo.findById(saved.getRouteId())
                                            .cast(SalesRouteEntity.class)
                                            .map(r -> toProfile(saved, r))
                                            .defaultIfEmpty(toProfile(saved, null))
                                    : Mono.just(toProfile(saved, null)));
                });
    }

    public Mono<SalesmanProfile> updateSalesman(String salesmanId, SalesmanProfile req) {
        return salesmanRepo.findAll()
                .cast(SalesmanEntity.class)
                .filter(e -> e.getSalesmanId().equals(salesmanId))
                .next()
                .flatMap(e -> {
                    if (req.getFullName() != null)  e.setFullName(req.getFullName());
                    if (req.getPhone() != null)      e.setPhone(req.getPhone());
                    if (req.getRouteId() != null)    e.setRouteId(req.getRouteId());
                    if (req.getIsActive() != null)   e.setIsActive(req.getIsActive());
                    return salesmanRepo.update(salesmanId, e)
                            .then(getSalesmanById(salesmanId));
                });
    }

    // ── Routes ─────────────────────────────────────────────────────────

    public Flux<SalesRoute> getAllRoutes() {
        return routeRepo.findAll()
                .cast(SalesRouteEntity.class)
                .map(this::toRoute);
    }

    // ── Daily Targets ──────────────────────────────────────────────────

    public Flux<DailyTarget> getTargets(
            String salesmanId, LocalDate from, LocalDate to) {
        return targetRepo.findFiltered(salesmanId, from, to)
                .flatMap(e -> getSalesmanById(e.getSalesmanId())
                        .map(s -> toTarget(e, s.getFullName()))
                        .defaultIfEmpty(toTarget(e, e.getSalesmanId())));
    }

    public Mono<DailyTarget> setTarget(DailyTarget req) {
        LocalDate date = req.getTargetDate() != null ? req.getTargetDate() : LocalDate.now();

        // Upsert — update if exists for this salesman+date, else create
        return targetRepo.findByDateAndSalesman(req.getSalesmanId(), date)
                .flatMap(existing -> {
                    existing.setVisitTarget(req.getVisitTarget() != null
                            ? req.getVisitTarget() : 0);
                    existing.setOrderTarget(req.getOrderTarget() != null
                            ? req.getOrderTarget() : 0);
                    existing.setCollectionTarget(req.getCollectionTarget() != null
                            ? BigDecimal.valueOf(req.getCollectionTarget()) : BigDecimal.ZERO);
                    return targetRepo.updateTarget(existing)
                            .then(Mono.just(toTarget(existing, null)));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    DailyTargetEntity e = new DailyTargetEntity();
                    e.setTargetId("TGT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    e.setSalesmanId(req.getSalesmanId());
                    e.setTargetDate(date);
                    e.setVisitTarget(req.getVisitTarget() != null ? req.getVisitTarget() : 0);
                    e.setOrderTarget(req.getOrderTarget() != null ? req.getOrderTarget() : 0);
                    e.setCollectionTarget(req.getCollectionTarget() != null
                            ? BigDecimal.valueOf(req.getCollectionTarget()) : BigDecimal.ZERO);
                    return targetRepo.create(e, DailyTargetEntity.class)
                            .cast(DailyTargetEntity.class)
                            .map(saved -> toTarget(saved, null));
                }));
    }

    public Mono<Void> deleteTarget(String targetId) {
        return targetRepo.deleteById(targetId).then();
    }

    // ── Converters ─────────────────────────────────────────────────────

    private SalesmanProfile toProfile(SalesmanEntity e, SalesRouteEntity route) {
        SalesmanProfile p = new SalesmanProfile();
        p.setSalesmanId(e.getSalesmanId());
        p.setUsername(e.getUsername());
        p.setFullName(e.getFullName());
        p.setPhone(e.getPhone());
        p.setRouteId(e.getRouteId());
        p.setIsActive(e.getIsActive());
        if (route != null) {
            p.setRouteName(route.getRouteName());
            p.setAreaName(route.getAreaName());
        }
        return p;
    }

    private SalesRoute toRoute(SalesRouteEntity e) {
        SalesRoute r = new SalesRoute();
        r.setRouteId(e.getRouteId());
        r.setRouteName(e.getRouteName());
        r.setAreaName(e.getAreaName());
        r.setIsActive(e.getIsActive());
        return r;
    }

    private DailyTarget toTarget(DailyTargetEntity e, String salesmanName) {
        DailyTarget t = new DailyTarget();
        t.setTargetId(e.getTargetId());
        t.setSalesmanId(e.getSalesmanId());
        t.setSalesmanName(salesmanName);
        t.setTargetDate(e.getTargetDate());
        t.setVisitTarget(e.getVisitTarget());
        t.setOrderTarget(e.getOrderTarget());
        t.setCollectionTarget(e.getCollectionTarget() != null
                ? e.getCollectionTarget().doubleValue() : 0.0);
        return t;
    }
}
