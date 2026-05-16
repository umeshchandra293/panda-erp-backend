package com.hst.materialmgmt.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hst.api.model.Product;
import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.product.ProductObjectMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.product.ProductRepository;
import com.hst.materialmgmt.service.SingleEntityBaseService;
import reactor.core.publisher.Mono;

@Service
public class ProductService extends SingleEntityBaseService {

    @Autowired private ProductRepository productRepository;
    @Autowired private ProductObjectMapper productMapper;
    @Autowired private ProductCodeGenerator codeGenerator;

    @Override protected BaseMapper getMapper() { return productMapper; }
    @Override protected ParentRepositoryImpl getMasterRepository() { return productRepository; }

    @Override
    public Mono<Object> createFullHierarchy(Mono<Object> parentMono) {
        if (parentMono == null)
            return Mono.error(new IllegalArgumentException("Product cannot be null"));

        Mono<Object> codedMono = parentMono.flatMap(model -> {
            Product product = (Product) model;
            return codeGenerator.nextProductCode().map(code -> {
                product.setProductId(code);
                return (Object) product;
            });
        });
        return super.createFullHierarchy(codedMono);
    }
}