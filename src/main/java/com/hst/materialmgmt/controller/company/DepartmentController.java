package com.hst.materialmgmt.controller.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.hst.api.DepartmentsApi;
import com.hst.api.model.Department;
import com.hst.materialmgmt.controller.BaseController;
import com.hst.materialmgmt.service.company.DepartmentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "Department API", description = "Endpoints for department operations")
public class DepartmentController extends BaseController implements DepartmentsApi {
	  @Autowired
	  private  DepartmentService departmentService;
	  
	  @Override
	  public Mono<ResponseEntity<Void>> deleteDepartment(String departmentKey, ServerWebExchange exchange) {
	    return delete(departmentService, departmentKey, exchange);
	  }

	  @Override
	  public Mono<ResponseEntity<Department>> getAllDepartments(ServerWebExchange exchange) {
		  return findAll(departmentService, exchange)
		      .cast(Department.class)
		      .next()
		      .map(ResponseEntity::ok)
		      .defaultIfEmpty(ResponseEntity.notFound().build())
		      .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
	  }
	  
	  /**
	   * Retrieves an department by its unique key.
	   *
	   * @param orgKey The unique key of the department to retrieve.
	   * @param exchange The server web exchange.
	   * @return A Mono that emits the department wrapped in a ResponseEntity.
	   */
	  @Override
	  public Mono<ResponseEntity<Department>> getDepartmentById(String departmentKey, ServerWebExchange exchange) {
	    return findByKey(departmentService, departmentKey, exchange)
	        .cast(Department.class) // Casts the Mono<Object> to Mono<Department>
	        .map(ResponseEntity::ok)
	        .defaultIfEmpty(ResponseEntity.notFound().build());
	  }

	  /**
	   * Creates a new department.
	   *
	   * @param department The department to create.
	   * @param exchange The server web exchange.
	   * @return A Mono that emits the created department wrapped in a ResponseEntity.
	   */
	  @Override
	  public Mono<ResponseEntity<Department>> createDepartment (
	      Mono<Department> department, ServerWebExchange exchange) {
		  return create(departmentService, department.cast(Object.class), exchange)
	        .cast(Department.class) // Casts the Mono<Object> to Mono<Department>
	        .map(newOrg ->ResponseEntity.status(HttpStatus.CREATED)
	        .body(newOrg)); // 201 Created for new resource
	  }

	  @Override
	  public Mono<ResponseEntity<Department>> updateDepartment(String departmentKey, Mono<Department> department, ServerWebExchange exchange) {
	    return update(departmentService, departmentKey, department.cast(Object.class), exchange)
	        .cast(Department.class) // Casts the Mono<Object> to Mono<Department>
	        .map(ResponseEntity::ok) // 200 OK with the updated Department
	        .defaultIfEmpty(ResponseEntity.notFound()
	        .build()); // 404 Not Found if the Department to update doesn't exist
	  }
}
