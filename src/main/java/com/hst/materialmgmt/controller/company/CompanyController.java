package com.hst.materialmgmt.controller.company;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.hst.api.CompanyApi;
import com.hst.api.model.Company;
import com.hst.materialmgmt.controller.BaseController;
import com.hst.materialmgmt.service.company.CompanyService;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "Company API", description = "Endpoints for company operations")
public class CompanyController extends BaseController implements CompanyApi {

    @Autowired
    private CompanyService companyService;

    @Override
    public Mono<ResponseEntity<Void>> deleteCompany(
            String companyKey, ServerWebExchange exchange) {
        return delete(companyService, companyKey, exchange);
    }

    @Override
    public Mono<ResponseEntity<Company>> getAllCompanies(
            ServerWebExchange exchange) {
        // CompanyApi interface returns single Company — we override with custom endpoint below
        return Mono.just(ResponseEntity.ok((Company) null));
    }

    // Custom endpoint returning all companies as a list
    @org.springframework.web.bind.annotation.GetMapping("/companies/all")
    public Mono<ResponseEntity<java.util.List<Company>>> getAllCompaniesList(
            ServerWebExchange exchange) {
        return findAll(companyService, exchange)
                .cast(Company.class)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(java.util.List.of()));
    }

    @Override
    public Mono<ResponseEntity<Company>> getCompanyById(
            String companyKey, ServerWebExchange exchange) {
        return findByKey(companyService, companyKey, exchange)
                .cast(Company.class)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Company>> createCompany(
            Mono<Company> company, ServerWebExchange exchange) {
        return create(companyService, company.cast(Object.class), exchange)
                .cast(Company.class)
                .map(newOrg -> ResponseEntity.status(HttpStatus.CREATED).body(newOrg));
    }

    @Override
    public Mono<ResponseEntity<Company>> updateCompany(
            String companyKey, Mono<Company> company, ServerWebExchange exchange) {
        return update(companyService, companyKey, company.cast(Object.class), exchange)
                .cast(Company.class)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
