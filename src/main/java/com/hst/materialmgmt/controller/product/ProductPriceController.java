package com.hst.materialmgmt.controller.product;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import com.hst.api.ProductPriceApi;
import com.hst.api.model.ProductPrice;
import com.hst.materialmgmt.controller.BaseController;
import com.hst.materialmgmt.service.product.ProductPriceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "Product Price API")
public class ProductPriceController extends BaseController implements ProductPriceApi {

    @Autowired private ProductPriceService productPriceService;

    @Override
    public Mono<ResponseEntity<ProductPrice>> getAllProductPrices(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok((ProductPrice) null));
    }

    @GetMapping("/product-prices/all")
    public Mono<ResponseEntity<List<ProductPrice>>> getAllProductPricesList(ServerWebExchange exchange) {
        return findAll(productPriceService, exchange)
                .cast(ProductPrice.class).collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(List.of()));
    }

    @Override
    public Mono<ResponseEntity<ProductPrice>> getProductPriceById(Integer productPriceId, ServerWebExchange exchange) {
        return findByKey(productPriceService, String.valueOf(productPriceId), exchange)
                .cast(ProductPrice.class).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> createProductPrice(Mono<ProductPrice> productPrice, ServerWebExchange exchange) {
        return create(productPriceService, productPrice.cast(Object.class), exchange)
                .cast(ProductPrice.class)
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).<Void>build());
    }

    @Override
    public Mono<ResponseEntity<Void>> updateProductPrice(Integer productPriceId, Mono<ProductPrice> productPrice, ServerWebExchange exchange) {
        return update(productPriceService, String.valueOf(productPriceId), productPrice.cast(Object.class), exchange)
                .cast(ProductPrice.class).map(p -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteProductPrice(Integer productPriceId, ServerWebExchange exchange) {
        return delete(productPriceService, String.valueOf(productPriceId), exchange);
    }
}
