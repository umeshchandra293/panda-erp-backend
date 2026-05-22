package com.hst.materialmgmt.controller.rawmaterial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import com.hst.api.GrnApi;
import com.hst.api.model.GrnRequest;
import com.hst.api.model.GrnResponse;
import com.hst.materialmgmt.service.RawMaterialService;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "GRN API")
public class GrnController implements GrnApi {

    @Autowired private RawMaterialService rawMaterialService;

    @Override
    public Mono<ResponseEntity<Flux<GrnResponse>>> getAllGrns(ServerWebExchange exchange) {
        Flux<GrnResponse> flux = rawMaterialService.getAllGrns();
        return Mono.just(ResponseEntity.ok(flux));
    }

    @Override
    public Mono<ResponseEntity<GrnResponse>> getGrnById(
            String grnId, ServerWebExchange exchange) {
        return rawMaterialService.getGrnById(grnId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<GrnResponse>> createGrn(
            Mono<GrnRequest> grnRequest, ServerWebExchange exchange) {
        return grnRequest.flatMap(req -> rawMaterialService.createGrn(req))
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }
}