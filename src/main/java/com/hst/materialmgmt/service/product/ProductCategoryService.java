package com.hst.materialmgmt.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hst.api.model.ProductCategory;
import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.product.ProductCategoryObjectMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.product.ProductCategoryRepository;
import com.hst.materialmgmt.service.SingleEntityBaseService;
import reactor.core.publisher.Mono;

@Service
public class ProductCategoryService extends SingleEntityBaseService {

    @Autowired private ProductCategoryRepository productCategoryRepository;
    @Autowired private ProductCategoryObjectMapper productCategoryMapper;
    @Autowired private ProductCategoryCodeGenerator codeGenerator;

    @Override protected BaseMapper getMapper() { return productCategoryMapper; }
    @Override protected ParentRepositoryImpl getMasterRepository() { return productCategoryRepository; }

    @Override
    public Mono<Object> createFullHierarchy(Mono<Object> parentMono) {
        if (parentMono == null)
            return Mono.error(new IllegalArgumentException("Category cannot be null"));

        Mono<Object> codedMono = parentMono.flatMap(model -> {
            ProductCategory cat = (ProductCategory) model;
            return codeGenerator.nextCategoryCode().map(code -> {
                cat.setCategoryId(code);
                return (Object) cat;
            });
        });
        return super.createFullHierarchy(codedMono);
    }
}