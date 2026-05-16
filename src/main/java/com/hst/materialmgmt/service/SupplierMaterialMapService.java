package com.hst.materialmgmt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.api.model.SupplierMaterialMapping;
import com.hst.materialmgmt.entity.SupplierMaterialMapEntity;
import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.SupplierMaterialMapMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.SupplierMaterialMapRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SupplierMaterialMapService extends ParentBaseServiceImpl {

    @Autowired
    private SupplierMaterialMapRepository repository;

    @Autowired
    private SupplierMaterialMapMapper mapper;

    @Override
    protected BaseMapper getMapper() {
        return mapper;
    }

    @Override
    protected ParentRepositoryImpl getParentRepository() {
        return repository;
    }

    /**
     * Returns all active mappings for the given supplier.
     * Powers: PO form material dropdown + SupplierDetailPage "Materials Supplied" table.
     */
    public Flux<SupplierMaterialMapping> getMappingsForSupplier(String supplierCode) {
        return repository.findBySupplierCode(supplierCode)
                .map(entity -> (SupplierMaterialMapping) mapper.toModel(entity));
    }

    /**
     * Creates a new mapping after checking for duplicates.
     * Injects supplierCode from the path into the model before delegating to base.
     */
    public Mono<SupplierMaterialMapping> createMapping(
            String supplierCode, SupplierMaterialMapping mapping) {

        // Ensure path param wins over anything in the body
        mapping.setSupplierCode(supplierCode);

        // Duplicate check: (supplierCode, materialId) must be unique
        return repository.findBySupplierAndMaterial(supplierCode, mapping.getMaterialId())
                .flatMap(existing ->
                        Mono.<SupplierMaterialMapping>error(
                                new DuplicateMappingException(supplierCode, mapping.getMaterialId())))
                .switchIfEmpty(
                        createFullHierarchy(Mono.just((Object) mapping))
                                .cast(SupplierMaterialMapping.class));
    }

    /**
     * Updates an existing mapping identified by supplierCode + materialId.
     */
    public Mono<SupplierMaterialMapping> updateMapping(
            String supplierCode, String materialId, SupplierMaterialMapping mapping) {

        mapping.setSupplierCode(supplierCode);
        mapping.setMaterialId(materialId);

        return repository.findBySupplierAndMaterial(supplierCode, materialId)
                .switchIfEmpty(Mono.error(
                        new RuntimeException("Mapping not found for supplier="
                                + supplierCode + " material=" + materialId)))
                .flatMap(existing ->
                        updateFullHierarchy(existing.getMappingId(), Mono.just((Object) mapping))
                                .cast(SupplierMaterialMapping.class));
    }

    /**
     * Removes a mapping by supplierCode + materialId.
     */
    public Mono<Void> deleteMapping(String supplierCode, String materialId) {
        return repository.findBySupplierAndMaterial(supplierCode, materialId)
                .switchIfEmpty(Mono.error(
                        new RuntimeException("Mapping not found for supplier="
                                + supplierCode + " material=" + materialId)))
                .flatMap(existing -> deleteFullHierarchy(existing.getMappingId()))
                .then();
    }

    // ---- inner exception class ----------------------------------------

    public static class DuplicateMappingException extends RuntimeException {
        public DuplicateMappingException(String supplierCode, String materialId) {
            super("Mapping already exists for supplier=" + supplierCode
                    + " material=" + materialId);
        }
    }
}
