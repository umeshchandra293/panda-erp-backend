package com.hst.materialmgmt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.hst.api.CompaniesApi;
import com.hst.api.model.Company;
import com.hst.materialmgmt.service.CompanyService;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "Company API", description = "Endpoints for company operations")
public class CompanyController extends BaseController implements CompaniesApi {
	  @Autowired
	  private  CompanyService companyService;
	  
	  @Override
	  public Mono<ResponseEntity<Void>> deleteCompany(String companyKey, ServerWebExchange exchange) {
	    return delete(companyService, companyKey, exchange);
	  }

	  @Override
	  public Mono<ResponseEntity<Company>> getAllCompanies(ServerWebExchange exchange) {
		  return findAll(companyService, exchange)
		      .cast(Company.class)
		      .next()
		      .map(ResponseEntity::ok)
		      .defaultIfEmpty(ResponseEntity.notFound().build())
		      .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
	  }
	  
	  /**
	   * Retrieves an company by its unique key.
	   *
	   * @param orgKey The unique key of the company to retrieve.
	   * @param exchange The server web exchange.
	   * @return A Mono that emits the company wrapped in a ResponseEntity.
	   */
	  @Override
	  public Mono<ResponseEntity<Company>> getCompanyById(String companyKey, ServerWebExchange exchange) {
	    return findByKey(companyService, companyKey, exchange)
	        .cast(Company.class) // Casts the Mono<Object> to Mono<Company>
	        .map(ResponseEntity::ok)
	        .defaultIfEmpty(ResponseEntity.notFound().build());
	  }

	  /**
	   * Creates a new Company.
	   *
	   * @param company The company to create.
	   * @param exchange The server web exchange.
	   * @return A Mono that emits the created company wrapped in a ResponseEntity.
	   */
	  @Override
	  public Mono<ResponseEntity<Company>> createCompany(
	      Mono<Company> company, ServerWebExchange exchange) {
		  return create(companyService, company.cast(Object.class), exchange)
	        .cast(Company.class) // Casts the Mono<Object> to Mono<Company>
	        .map(newOrg ->ResponseEntity.status(HttpStatus.CREATED)
	        .body(newOrg)); // 201 Created for new resource
	  }

	  @Override
	  public Mono<ResponseEntity<Company>> updateCompany(String companyKey, Mono<Company> company, ServerWebExchange exchange) {
	    return update(companyService, companyKey, company.cast(Object.class), exchange)
	        .cast(Company.class) // Casts the Mono<Object> to Mono<Company>
	        .map(ResponseEntity::ok) // 200 OK with the updated Company
	        .defaultIfEmpty(ResponseEntity.notFound()
	        .build()); // 404 Not Found if the Company to update doesn't exist
	  }
}

