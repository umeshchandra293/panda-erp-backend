package com.hst.materialmgmt.controller.fgstock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hst.materialmgmt.entity.fgstock.FgMovementEntity;
import com.hst.materialmgmt.entity.fgstock.FgStockEntity;
import com.hst.materialmgmt.service.fgstock.FgStockService;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt/fg-stock")
@Tag(name = "Finished Goods Stock API")
public class FgStockController {

    @Autowired private FgStockService service;

    @GetMapping
    public ResponseEntity<Flux<FgStockEntity>> getAllStock() {
        return ResponseEntity.ok(service.getAllStock());
    }

    @GetMapping("/ledger")
    public ResponseEntity<Flux<FgMovementEntity>> getLedger(
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        return ResponseEntity.ok(service.getLedger(productId, fromDate, toDate));
    }

    @PostMapping("/dispatch")
    public Mono<ResponseEntity<Void>> dispatch(@RequestBody DispatchRequest req) {
        return service.dispatch(req.productId(), req.quantity(),
                                req.referenceId(), req.notes())
                .<ResponseEntity<Void>>thenReturn(ResponseEntity.<Void>status(HttpStatus.CREATED).build())
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(ResponseEntity.<Void>badRequest().build()));
    }

    public record DispatchRequest(
        String productId,
        int    quantity,
        String referenceId,
        String notes
    ) {}
}