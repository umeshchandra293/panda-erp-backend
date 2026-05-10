package com.hst.materialmgmt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.hst.api.SupplierApi;
import com.hst.api.model.Supplier;
import com.hst.materialmgmt.service.SupplierService;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "Supplier API", description = "Endpoints for supplier operations")
public class SupplierController extends BaseController implements SupplierApi {
	
	  @Autowired
	  private  SupplierService supplierService;

	  @Override
	  public Mono<ResponseEntity<Void>> deleteSupplier(String supplierKey, ServerWebExchange exchange) {
	    return delete(supplierService, supplierKey, exchange);
	  }

	  @Override
	  public Mono<ResponseEntity<Supplier>> getAllSuppliers(ServerWebExchange exchange) {
		  return findAll(supplierService, exchange)
		      .cast(Supplier.class)
		      .next()
		      .map(ResponseEntity::ok)
		      .defaultIfEmpty(ResponseEntity.notFound().build())
		      .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
	  }
	  
	  /**
	   * Retrieves an supplier by its unique key.
	   *
	   * @param supplierKey The unique key of the supplier to retrieve.
	   * @param exchange The server web exchange.
	   * @return A Mono that emits the supplier wrapped in a ResponseEntity.
	   */
	  @Override
	  public Mono<ResponseEntity<Supplier>> getSupplierById(String supplierKey, ServerWebExchange exchange) {
	    return findByKey(supplierService, supplierKey, exchange)
	        .cast(Supplier.class) // Casts the Mono<Object> to Mono<Supplier>
	        .map(ResponseEntity::ok)
	        .defaultIfEmpty(ResponseEntity.notFound().build());
	  }

	  /**
	   * Creates a new supplier.
	   *
	   * @param supplier The supplier to create.
	   * @param exchange The server web exchange.
	   * @return A Mono that emits the created supplier wrapped in a ResponseEntity.
	   */
	  @Override
	  public Mono<ResponseEntity<Supplier>> createSupplier(Mono<Supplier> supplier, ServerWebExchange exchange) {
	    return create(supplierService, supplier.cast(Object.class), exchange)
	        .cast(Supplier.class) // Casts the Mono<Object> to Mono<Supplier>
	        .map(newSupplier ->ResponseEntity.status(HttpStatus.CREATED)
	        .body(newSupplier)); // 201 Created for new resource
	  }

	  @Override
	  public Mono<ResponseEntity<Supplier>> updateSupplier(String supplierKey, Mono<Supplier> supplier, ServerWebExchange exchange) {
	    return update(supplierService, supplierKey, supplier.cast(Object.class), exchange)
	        .cast(Supplier.class) // Casts the Mono<Object> to Mono<Supplier>
	        .map(ResponseEntity::ok) // 200 OK with the updated supplier
	        .defaultIfEmpty(ResponseEntity.notFound()
	        .build()); // 404 Not Found if the supplier to update doesn't exist
	  }
}
