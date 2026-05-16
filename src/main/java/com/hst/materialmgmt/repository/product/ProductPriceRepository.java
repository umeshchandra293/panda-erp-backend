package com.hst.materialmgmt.repository.product;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.entity.product.ProductPriceEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.product.ProductPriceRowMapper;

@Repository
public class ProductPriceRepository extends ParentRepositoryImpl {
    private static final String TABLE_NAME     = "product_price_tbl";
    private static final String TABLE_NAME_KEY = "product_price_id";

    @Autowired private ProductPriceRowMapper productPriceRowMapper;

    @SuppressWarnings("unchecked")
    @Override protected Class<ProductPriceEntity> getEntityClass() { return ProductPriceEntity.class; }
    @SuppressWarnings("unchecked")
    @Override protected BaseRowMapper<ProductPriceEntity> getRowMapper() { return productPriceRowMapper; }
    @Override protected String getTableName() { return TABLE_NAME; }
    @Override protected Map<String, Object> getKeyParamMap(String id) { return Map.of(TABLE_NAME_KEY, id); }
}
