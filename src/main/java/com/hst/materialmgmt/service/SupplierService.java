package com.hst.materialmgmt.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.materialmgmt.entity.AddressEntity;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.EmailEntity;
import com.hst.materialmgmt.entity.PhoneEntity;
import com.hst.materialmgmt.entity.SupplierAddressEntity;
import com.hst.materialmgmt.entity.SupplierEmailEntity;
import com.hst.materialmgmt.entity.SupplierEntity;
import com.hst.materialmgmt.entity.SupplierPhoneEntity;
import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.SupplierMapper;
import com.hst.materialmgmt.repository.AddressRepository;
import com.hst.materialmgmt.repository.EmailRepository;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.PhoneRepository;
import com.hst.materialmgmt.repository.SupplierRepository;

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
		Map<ParentRepositoryImpl, List<BaseEntity>> childRepoMap = new HashMap<ParentRepositoryImpl, List<BaseEntity>>();
		if (parentEntity instanceof SupplierEntity) {
			SupplierEntity companyEntity = (SupplierEntity) parentEntity;
			childRepoMap.put(addressRepository, companyEntity.getAddressEntities());
			childRepoMap.put(phoneRepository, companyEntity.getPhoneEntities());
			childRepoMap.put(emailRepository, companyEntity.getEmailEntities());
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
	    List<ParentRepositoryImpl> repoList = new ArrayList<ParentRepositoryImpl>();
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
	    List<BaseEntity> entities = children.stream().map(obj -> (BaseEntity) obj).collect(Collectors.toList());
	    
	    if (parentEntity instanceof SupplierEntity) {
	    	SupplierEntity supplierEntity = (SupplierEntity) parentEntity;
	        if (childEntity instanceof AddressEntity) {
	        	supplierEntity.setAddressEntities(entities);
	        } else if (childEntity instanceof PhoneEntity) {
	        	supplierEntity.setPhoneEntities(entities);
	        } else if (childEntity instanceof EmailEntity) {
	        	supplierEntity.setEmailEntities(entities);
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
