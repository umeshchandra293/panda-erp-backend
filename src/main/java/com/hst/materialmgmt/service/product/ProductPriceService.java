package com.hst.materialmgmt.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.product.ProductPriceObjectMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.product.ProductPriceRepository;
import com.hst.materialmgmt.service.SingleEntityBaseService;

@Service
public class ProductPriceService extends SingleEntityBaseService {
    
    @Autowired 
    private ProductPriceRepository productPriceRepository;
    
    @Autowired
    private ProductPriceObjectMapper productPriceMapper;

    @Override
    protected BaseMapper getMapper() {
        return productPriceMapper;
    }

    @Override
    protected ParentRepositoryImpl getMasterRepository() {
        return productPriceRepository;
    }
}
