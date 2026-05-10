package com.hst.materialmgmt.objectMapper.company;

import org.springframework.stereotype.Component;

import com.hst.api.model.Company;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.company.CompanyEntity;
import com.hst.materialmgmt.objectMapper.BaseMapper;

@Component
public class CompanyMapper extends BaseMapper {

	@Override
	public BaseEntity toEntity(Object modelObject, Object entityObject, boolean isNew) {
		Company company = (Company) modelObject;

	    CompanyEntity updateEntity = null;

	    if (isNew || entityObject == null) {
	      updateEntity = new CompanyEntity();
	    } else {
	      updateEntity = (CompanyEntity) entityObject;
	    }

	    updateEntity.setCompanyId(company.getCompanyId());
	    updateEntity.setName(company.getName());
	    updateEntity.setLegalName(company.getLegalName());
		updateEntity.setTaxId(company.getTaxId());
		updateEntity.setEmailId(company.getEmailId());
		updateEntity.setPhone(company.getPhone());
		updateEntity.setWebsite(company.getWebsite());
		updateEntity.setAddress(company.getAddress());
	    return updateEntity;
	}
	 
	@Override
	public Object toModel(Object entityObject) {
		if (entityObject == null) {
	      return null;
	    }
	    CompanyEntity companyEntity = (CompanyEntity) entityObject;
	    Company company = new Company();
	    company.setCompanyId(companyEntity.getCompanyId());
	    company.setName(companyEntity.getName());
	    company.setLegalName(companyEntity.getLegalName());
	    company.setTaxId(companyEntity.getTaxId());
	    company.setEmailId(companyEntity.getEmailId());
	    company.setPhone(companyEntity.getPhone());
	    company.setWebsite(companyEntity.getWebsite());
	    company.setAddress(companyEntity.getAddress());
	    return company;
	}
}
