package com.hst.materialmgmt.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hst.api.model.ProductPrice;
import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.product.ProductPriceObjectMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.product.ProductPriceRepository;
import com.hst.materialmgmt.service.SingleEntityBaseService;
import reactor.core.publisher.Mono;

@Service
public class ProductPriceService extends SingleEntityBaseService {

    @Autowired private ProductPriceRepository productPriceRepository;
    @Autowired private ProductPriceObjectMapper productPriceMapper;
    @Autowired private ProductPriceCodeGenerator codeGenerator;

    @Override protected BaseMapper getMapper() { return productPriceMapper; }
    @Override protected ParentRepositoryImpl getMasterRepository() { return productPriceRepository; }

    @Override
    public Mono<Object> createFullHierarchy(Mono<Object> parentMono) {
        if (parentMono == null)
            return Mono.error(new IllegalArgumentException("Price cannot be null"));

        Mono<Object> codedMono = parentMono.flatMap(model -> {
            ProductPrice price = (ProductPrice) model;
            return codeGenerator.nextPriceCode().map(code -> {
                price.setProductPriceId(code);
                return (Object) price;
            });
        });
        return super.createFullHierarchy(codedMono);
    }
}