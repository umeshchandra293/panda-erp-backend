package com.hst.materialmgmt.repository;

import com.hst.materialmgmt.entity.SupplierAddressEntity;
import com.hst.materialmgmt.entity.SupplierEmailEntity;
import com.hst.materialmgmt.entity.SupplierPhoneEntity;

public class RepositoryConstants {
	
	public static final Class<SupplierAddressEntity> SUPPLIER_ADDRESS_ENTITY_CLASS = SupplierAddressEntity.class;
	public static final Class<SupplierPhoneEntity>   SUPPLIER_PHONE_ENTITY_CLASS   = SupplierPhoneEntity.class;
	public static final Class<SupplierEmailEntity>   SUPPLIER_EMAIL_ENTITY_CLASS   = SupplierEmailEntity.class;

	public static final String SUPPLIER_ADDRESS_LINK 	= "rm_supplier_address_link";
	public static final String SUPPLIER_PHONE_LINK 		= "rm_supplier_phone_link";
	public static final String SUPPLIER_EMAIL_LINK 		= "rm_supplier_email_link";

}
