package com.hst.materialmgmt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.hst.api.MaterialOrdersApi;
import com.hst.api.model.MaterialOrder;
import com.hst.api.model.MaterialOrderPage;
import com.hst.api.model.Supplier;
import com.hst.materialmgmt.service.SupplierService;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "Material Order API", description = "Endpoints for material order operations")

public class MaterialOrderController extends BaseController implements MaterialOrdersApi {
	  @Autowired
	  private  SupplierService supplierService;

	  @Override
	  public Mono<ResponseEntity<Void>> deleteMaterialOrder(String supplierKey, ServerWebExchange exchange) {
	    return delete(supplierService, supplierKey, exchange);
	  }

	  @Override
	  public Mono<ResponseEntity<MaterialOrderPage>> getAllMaterialOrders(ServerWebExchange exchange) {
		  return findAll(supplierService, exchange)
		      .cast(MaterialOrderPage.class)
		      .next()
		      .map(ResponseEntity::ok)
		      .defaultIfEmpty(ResponseEntity.notFound().build())
		      .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
	  }
	  
	  /**
	   * Retrieves an organization by its unique key.
	   *
	   * @param orgKey The unique key of the organization to retrieve.
	   * @param exchange The server web exchange.
	   * @return A Mono that emits the organization wrapped in a ResponseEntity.
	   */
	  @Override
	  public Mono<ResponseEntity<MaterialOrder>> getMaterialOrderById(String supplierKey, ServerWebExchange exchange) {
	    return findByKey(supplierService, supplierKey, exchange)
	        .cast(MaterialOrder.class) // Casts the Mono<Object> to Mono<Organization>
	        .map(ResponseEntity::ok)
	        .defaultIfEmpty(ResponseEntity.notFound().build());
	  }

	  /**
	   * Creates a new organization.
	   *
	   * @param organization The organization to create.
	   * @param exchange The server web exchange.
	   * @return A Mono that emits the created organization wrapped in a ResponseEntity.
	   */
	  @Override
	  public Mono<ResponseEntity<MaterialOrder>> createMaterialOrder(
	      Mono<MaterialOrder> materialOrder, ServerWebExchange exchange) {
	    return create(supplierService, materialOrder.cast(Object.class), exchange)
	        .cast(MaterialOrder.class) // Casts the Mono<Object> to Mono<Organization>
	        .map(
	            newOrg ->
	                ResponseEntity.status(HttpStatus.CREATED)
	                    .body(newOrg)); // 201 Created for new resource
	  }

	  @Override
	  public Mono<ResponseEntity<MaterialOrder>> updateMaterialOrder(String supplierKey, Mono<MaterialOrder> supplier, ServerWebExchange exchange) {
	    return update(supplierService, supplierKey, supplier.cast(Object.class), exchange)
	        .cast(MaterialOrder.class) // Casts the Mono<Object> to Mono<Organization>
	        .map(ResponseEntity::ok) // 200 OK with the updated organization
	        .defaultIfEmpty(
	            ResponseEntity.notFound()
	                .build()); // 404 Not Found if the organization to update doesn't exist
	  }
}
