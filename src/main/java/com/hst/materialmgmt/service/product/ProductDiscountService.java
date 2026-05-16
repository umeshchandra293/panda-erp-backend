package com.hst.materialmgmt.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hst.api.model.ProductDiscount;
import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.product.ProductDiscountObjectMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.product.ProductDiscountRepository;
import com.hst.materialmgmt.service.SingleEntityBaseService;
import reactor.core.publisher.Mono;

@Service
public class ProductDiscountService extends SingleEntityBaseService {

    @Autowired private ProductDiscountRepository productDiscountRepository;
    @Autowired private ProductDiscountObjectMapper productDiscountMapper;
    @Autowired private ProductDiscountCodeGenerator codeGenerator;

    @Override protected BaseMapper getMapper() { return productDiscountMapper; }
    @Override protected ParentRepositoryImpl getMasterRepository() { return productDiscountRepository; }

    @Override
    public Mono<Object> createFullHierarchy(Mono<Object> parentMono) {
        if (parentMono == null)
            return Mono.error(new IllegalArgumentException("Discount cannot be null"));

        Mono<Object> codedMono = parentMono.flatMap(model -> {
            ProductDiscount discount = (ProductDiscount) model;
            return codeGenerator.nextDiscountCode().map(code -> {
                discount.setDiscountId(code);
                return (Object) discount;
            });
        });
        return super.createFullHierarchy(codedMono);
    }
}