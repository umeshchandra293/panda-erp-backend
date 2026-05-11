package com.hst.materialmgmt.repository.product;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.product.ProductEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.product.ProductRowMapper;

@Repository
public class ProductRepository extends ParentRepositoryImpl {
    private static final String TABLE_NAME = "product_tbl";

    private static final String TABLE_NAME_KEY = "product_id";
	
	@Autowired 
  	private ProductRowMapper productRowMapper;

    @SuppressWarnings("unchecked")
    @Override
    protected Class<ProductEntity> getEntityClass() {
        return ProductEntity.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected BaseRowMapper<ProductEntity> getRowMapper() {
        return productRowMapper;
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
