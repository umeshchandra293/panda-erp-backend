package com.hst.materialmgmt.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.product.ProductDiscountObjectMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.product.ProductDiscountRepository;
import com.hst.materialmgmt.service.SingleEntityBaseService;

@Service
public class ProductDiscountService extends SingleEntityBaseService {
    
    @Autowired 
    private ProductDiscountRepository productDiscountRepository;
    
    @Autowired
    private ProductDiscountObjectMapper productDiscountMapper;

    @Override
    protected BaseMapper getMapper() {
        return productDiscountMapper;
    }

    @Override
    protected ParentRepositoryImpl getMasterRepository() {
        return productDiscountRepository;
    }
}