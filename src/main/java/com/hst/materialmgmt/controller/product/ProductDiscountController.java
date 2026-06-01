package com.hst.materialmgmt.controller.product;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import com.hst.api.MarketingPromotionsApi;
import com.hst.api.model.ProductDiscount;
import com.hst.materialmgmt.controller.BaseController;
import com.hst.materialmgmt.service.product.ProductDiscountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "Product Discount API")
public class ProductDiscountController extends BaseController implements MarketingPromotionsApi {

    @Autowired private ProductDiscountService productDiscountService;

    @Override
    public Mono<ResponseEntity<ProductDiscount>> getAllProductDiscounts(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok((ProductDiscount) null));
    }

    @GetMapping("/product-discounts/all")
    public Mono<ResponseEntity<List<ProductDiscount>>> getAllProductDiscountsList(ServerWebExchange exchange) {
        return findAll(productDiscountService, exchange)
                .cast(ProductDiscount.class).collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(List.of()));
    }

    @Override
    public Mono<ResponseEntity<ProductDiscount>> getProductDiscountById(
            String discountId, ServerWebExchange exchange) {
        return findByKey(productDiscountService, discountId, exchange)
                .cast(ProductDiscount.class).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> createProductDiscount(
            Mono<ProductDiscount> productDiscount, ServerWebExchange exchange) {
        return create(productDiscountService, productDiscount.cast(Object.class), exchange)
                .cast(ProductDiscount.class)
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).<Void>build());
    }

    @Override
    public Mono<ResponseEntity<Void>> updateProductDiscount(
            String discountId, Mono<ProductDiscount> productDiscount, ServerWebExchange exchange) {
        return update(productDiscountService, discountId,
                productDiscount.cast(Object.class), exchange)
                .cast(ProductDiscount.class)
                .map(p -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteProductDiscount(
            String discountId, ServerWebExchange exchange) {
        return delete(productDiscountService, discountId, exchange);
    }
}