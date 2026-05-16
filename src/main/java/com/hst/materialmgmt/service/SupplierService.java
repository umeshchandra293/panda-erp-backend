package com.hst.materialmgmt.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.api.model.Supplier;
import com.hst.materialmgmt.entity.AddressEntity;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.EmailEntity;
import com.hst.materialmgmt.entity.PhoneEntity;
import com.hst.materialmgmt.entity.supplier.SupplierAddressEntity;
import com.hst.materialmgmt.entity.supplier.SupplierEmailEntity;
import com.hst.materialmgmt.entity.supplier.SupplierEntity;
import com.hst.materialmgmt.entity.supplier.SupplierPhoneEntity;
import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.SupplierMapper;
import com.hst.materialmgmt.repository.AddressRepository;
import com.hst.materialmgmt.repository.EmailRepository;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.PhoneRepository;
import com.hst.materialmgmt.repository.SupplierRepository;

import reactor.core.publisher.Mono;

@Service
public class SupplierService extends BaseServiceImpl {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private SupplierMapper supplierMapper;

    @Autowired
    private SupplierCodeGenerator supplierCodeGenerator;

    // ------------------------------------------------------------------
    // Override CREATE to inject a server-generated supplier_code before
    // the base class converts the model to an entity.
    // ------------------------------------------------------------------

    @Override
    public Mono<Object> createFullHierarchy(Mono<Object> parentMono) {
        if (parentMono == null) {
            return Mono.error(new IllegalArgumentException("Parent object cannot be null"));
        }

        Mono<Object> codedMono = parentMono.flatMap(model -> {
            Supplier supplier = (Supplier) model;
            // Defensive: the OpenAPI marks supplierCode readOnly, but if a client
            // sends one anyway we ignore it and assign a fresh sequential code.
            return supplierCodeGenerator.nextSupplierCode()
                    .map(code -> {
                        supplier.setSupplierCode(code);
                        return (Object) supplier;
                    });
        });

        return super.createFullHierarchy(codedMono);
    }

    // ------------------------------------------------------------------
    // Hierarchy wiring (unchanged)
    // ------------------------------------------------------------------

    @Override
    protected BaseMapper getMapper() {
        return supplierMapper;
    }

    @Override
    protected ParentRepositoryImpl getMasterRepository() {
        return supplierRepository;
    }

    @Override
    protected Map<ParentRepositoryImpl, List<BaseEntity>> getChildReposAndEntities(BaseEntity parentEntity) {
        Map<ParentRepositoryImpl, List<BaseEntity>> childRepoMap = new HashMap<>();
        if (parentEntity instanceof SupplierEntity) {
            SupplierEntity supplier = (SupplierEntity) parentEntity;
            childRepoMap.put(addressRepository, supplier.getAddressEntities());
            childRepoMap.put(phoneRepository, supplier.getPhoneEntities());
            childRepoMap.put(emailRepository, supplier.getEmailEntities());
        }
        return childRepoMap;
    }

    @Override
    protected BaseEntity getParentChildLinkObject(Object parentObject, Object childObject) {
        String parentId = getIdFromEntityObject(parentObject);
        String childId = getIdFromEntityObject(childObject);

        if (parentId == null || childId == null) {
            return null;
        }

        if (childObject instanceof AddressEntity) {
            return SupplierAddressEntity.builder().parentId(parentId).childId(childId).build();
        } else if (childObject instanceof PhoneEntity) {
            return SupplierPhoneEntity.builder().parentId(parentId).childId(childId).build();
        } else if (childObject instanceof EmailEntity) {
            return SupplierEmailEntity.builder().parentId(parentId).childId(childId).build();
        }
        return null;
    }

    @Override
    protected List<ParentRepositoryImpl> getChildRepositoryList(BaseEntity parentEntity) {
        List<ParentRepositoryImpl> repoList = new ArrayList<>();
        repoList.add(addressRepository);
        repoList.add(phoneRepository);
        repoList.add(emailRepository);
        return repoList;
    }

    @Override
    protected void attachChildrenToParent(Object parentEntity, List<Object> children) {
        if (children == null || children.isEmpty()) {
            return;
        }

        BaseEntity childEntity = (BaseEntity) children.get(0);
        List<BaseEntity> entities = children.stream()
                .map(obj -> (BaseEntity) obj)
                .collect(Collectors.toList());

        if (parentEntity instanceof SupplierEntity) {
            SupplierEntity supplier = (SupplierEntity) parentEntity;
            if (childEntity instanceof AddressEntity) {
                supplier.setAddressEntities(entities);
            } else if (childEntity instanceof PhoneEntity) {
                supplier.setPhoneEntities(entities);
            } else if (childEntity instanceof EmailEntity) {
                supplier.setEmailEntities(entities);
            }
        }
    }

    @Override
    protected ParentRepositoryImpl getRepositoryForEntity(BaseEntity entity) {
        if (entity instanceof AddressEntity) {
            return addressRepository;
        } else if (entity instanceof PhoneEntity) {
            return phoneRepository;
        } else if (entity instanceof EmailEntity) {
            return emailRepository;
        }
        return null;
    }
}
