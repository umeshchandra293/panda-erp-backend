package com.hst.materialmgmt.repository.product;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.product.ProductCategoryEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.product.ProductCategoryRowMapper;

@Repository
public class ProductCategoryRepository extends ParentRepositoryImpl {
    private static final String TABLE_NAME = "product_category_tbl";
    private static final String TABLE_NAME_KEY = "category_id";
	
	@Autowired 
  	private ProductCategoryRowMapper productCategoryRowMapper;


    @SuppressWarnings("unchecked")
    @Override
    protected Class<ProductCategoryEntity> getEntityClass() {
        return ProductCategoryEntity.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected BaseRowMapper<ProductCategoryEntity> getRowMapper() {
        return productCategoryRowMapper;
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
