package com.hst.materialmgmt.repository.product;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.product.ProductDiscountEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.product.ProductDiscountRowMapper;

@Repository
public class ProductDiscountRepository extends ParentRepositoryImpl {
    private static final String TABLE_NAME = "product_discount_tbl";

    private static final String TABLE_NAME_KEY = "product_discount_id";
	
	@Autowired 
  	private ProductDiscountRowMapper productDiscountRowMapper;


    @Override
    @SuppressWarnings("unchecked")
    protected Class<ProductDiscountEntity> getEntityClass() {
        return ProductDiscountEntity.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected BaseRowMapper<ProductDiscountEntity> getRowMapper() {
        return productDiscountRowMapper;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected Map<String, Object> getKeyParamMap(String id) {
		Map<String, Object> keyParams = Map.of(TABLE_NAME_KEY, id);
		return keyParams;
    }

}
