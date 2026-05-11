package com.hst.materialmgmt.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.product.ProductObjectMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.product.ProductRepository;
import com.hst.materialmgmt.service.SingleEntityBaseService;

@Service
public class ProductService extends SingleEntityBaseService {
    @Autowired 
    private ProductRepository productRepository;
    
    @Autowired
    private ProductObjectMapper productMapper;

    @Override
    protected BaseMapper getMapper() {
        return productMapper;
    }

    @Override
    protected ParentRepositoryImpl getMasterRepository() {
        return productRepository;
    }
}
