package com.hst.materialmgmt.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.product.ProductCategoryObjectMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.product.ProductCategoryRepository;
import com.hst.materialmgmt.service.SingleEntityBaseService;

@Service
public class ProductCategoryService extends SingleEntityBaseService {
    
    @Autowired 
    private ProductCategoryRepository productCategoryRepository;
    
    @Autowired
    private ProductCategoryObjectMapper productCategoryMapper;

    @Override
    protected BaseMapper getMapper() {
        return productCategoryMapper;
    }

    @Override
    protected ParentRepositoryImpl getMasterRepository() {
        return productCategoryRepository;
    }
}
