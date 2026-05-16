package com.hst.materialmgmt.controller.product;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import com.hst.api.ProductsApi;
import com.hst.api.model.Product;
import com.hst.materialmgmt.controller.BaseController;
import com.hst.materialmgmt.service.product.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "Product API")
public class ProductController extends BaseController implements ProductsApi {

    @Autowired private ProductService productService;

    @Override
    public Mono<ResponseEntity<Product>> getAllProducts(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok((Product) null));
    }

    @GetMapping("/products/all")
    public Mono<ResponseEntity<List<Product>>> getAllProductsList(ServerWebExchange exchange) {
        return findAll(productService, exchange)
                .cast(Product.class).collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(List.of()));
    }

    @Override
    public Mono<ResponseEntity<Product>> getProductById(Integer productId, ServerWebExchange exchange) {
        return findByKey(productService, String.valueOf(productId), exchange)
                .cast(Product.class).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> createProduct(Mono<Product> product, ServerWebExchange exchange) {
        return create(productService, product.cast(Object.class), exchange)
                .cast(Product.class)
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).<Void>build());
    }

    @Override
    public Mono<ResponseEntity<Void>> updateProduct(Integer productId, Mono<Product> product, ServerWebExchange exchange) {
        return update(productService, String.valueOf(productId), product.cast(Object.class), exchange)
                .cast(Product.class).map(p -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteProduct(Integer productId, ServerWebExchange exchange) {
        return delete(productService, String.valueOf(productId), exchange);
    }
}
