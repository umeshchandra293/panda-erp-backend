package com.hst.materialmgmt.controller.product;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import com.hst.api.CategoriesApi;
import com.hst.api.ProductCategoriesApi;
import com.hst.api.model.ProductCategory;
import com.hst.materialmgmt.controller.BaseController;
import com.hst.materialmgmt.service.product.ProductCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "Product Category API")
public class ProductCategoryController extends BaseController
        implements ProductCategoriesApi, CategoriesApi {

    @Autowired private ProductCategoryService productCategoryService;

    // ── ProductCategoriesApi ──────────────────────────────────────────────

    @Override
    public Mono<ResponseEntity<ProductCategory>> getAllCategories(
            ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok((ProductCategory) null));
    }

    @GetMapping("/product-categories/all")
    public Mono<ResponseEntity<List<ProductCategory>>> getAllCategoriesList(
            ServerWebExchange exchange) {
        return findAll(productCategoryService, exchange)
                .cast(ProductCategory.class).collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(List.of()));
    }

    @Override
    public Mono<ResponseEntity<ProductCategory>> getCategoryById(
            Integer categoryId, ServerWebExchange exchange) {
        return findByKey(productCategoryService, String.valueOf(categoryId), exchange)
                .cast(ProductCategory.class).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> updateCategory(
            Integer categoryId, Mono<ProductCategory> productCategory,
            ServerWebExchange exchange) {
        return update(productCategoryService, String.valueOf(categoryId),
                productCategory.cast(Object.class), exchange)
                .cast(ProductCategory.class)
                .map(p -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteCategory(
            Integer categoryId, ServerWebExchange exchange) {
        return delete(productCategoryService, String.valueOf(categoryId), exchange);
    }

    // ── CategoriesApi (createCategory lives here) ─────────────────────────

    @Override
    public Mono<ResponseEntity<Void>> createCategory(
            Mono<ProductCategory> productCategory, ServerWebExchange exchange) {
        return create(productCategoryService, productCategory.cast(Object.class), exchange)
                .cast(ProductCategory.class)
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).<Void>build());
    }
}
