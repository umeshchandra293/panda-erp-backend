package com.hst.materialmgmt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;

import com.hst.materialmgmt.service.BaseService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class BaseController {

    /**
     * Maps a completion signal to a 204 No Content response.
     * We use <Void> explicitly to match the Controller requirements.
     */
    protected Mono<ResponseEntity<Void>> convert(Mono<?> completionSignal) {
        return completionSignal.then(Mono.just(ResponseEntity.noContent().build()));
    }

    /**
     * Handles the deletion logic.
     */
    protected Mono<ResponseEntity<Void>> delete(BaseService baseService, String key, ServerWebExchange exchange) {
        if (key == null || key.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Object key cannot be null or empty"));
        }

        return baseService.deleteFullHierarchy(key)
            // Use .then() to ignore the service result and emit the 204 response
            .then(Mono.fromCallable(() -> ResponseEntity.noContent().<Void>build()))
            // Standardize the error response type
            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<Void>build()));
    }

    protected Flux<Object> findAll(BaseService baseService, ServerWebExchange exchange) {
        return baseService.findAllFullHierarchy();
    }

    protected Mono<Object> findByKey(BaseService baseService, String key, ServerWebExchange exchange) {
        if (key == null || key.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Object key cannot be null or empty"));
        }
        return baseService.findByIdFullHierarchy(key);
    }

    protected Mono<Object> create(BaseService baseService, Mono<Object> monoObject, ServerWebExchange exchange) {
        if (monoObject == null) {
            return Mono.error(new IllegalArgumentException("Object cannot be null"));
        }
        return baseService.createFullHierarchy(monoObject);
    }

    protected Mono<Object> update(BaseService baseService, String key, Mono<Object> monoObject, ServerWebExchange exchange) {
        if (key == null || key.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Key cannot be null"));
        }
        if (monoObject == null) {
            return Mono.error(new IllegalArgumentException("Object cannot be null"));
        }
        return baseService.updateFullHierarchy(key, monoObject);
    }
}
