package com.hst.materialmgmt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.hst.api.EmployeesApi;
import com.hst.api.model.Employee;
import com.hst.materialmgmt.service.EmployeeService;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "Employee API", description = "Endpoints for employee operations")
public class EmployeeController extends BaseController implements EmployeesApi {

	@Autowired
	private  EmployeeService employeeService;
  
	@Override
	public Mono<ResponseEntity<Void>> deleteEmployee(String employeeKey, ServerWebExchange exchange) {
		return delete(employeeService, employeeKey, exchange);
	}

	@Override
	public Mono<ResponseEntity<Employee>> getAllEmployees(ServerWebExchange exchange) {
	  return findAll(employeeService, exchange)
	      .cast(Employee.class)
	      .next()
	      .map(ResponseEntity::ok)
	      .defaultIfEmpty(ResponseEntity.notFound().build())
	      .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
	}
  
	/**
	 * Retrieves an employee by its unique key.
	 *
	   * @param employeeKey The unique key of the employee to retrieve.
	   * @param exchange The server web exchange.
	   * @return A Mono that emits the employee wrapped in a ResponseEntity.
	   */
	  @Override
	  public Mono<ResponseEntity<Employee>> getEmployeeById(String employeeKey, ServerWebExchange exchange) {
	    return findByKey(employeeService, employeeKey, exchange)
	        .cast(Employee.class) // Casts the Mono<Object> to Mono<Employee>
	        .map(ResponseEntity::ok)
	        .defaultIfEmpty(ResponseEntity.notFound().build());
	  }
	
	  /**
	   * Creates a new Employee.
	   *
	   * @param employee The employee to create.
	   * @param exchange The server web exchange.
	   * @return A Mono that emits the created employee wrapped in a ResponseEntity.
	   */
	  @Override
	  public Mono<ResponseEntity<Employee>> createEmployee(
	      Mono<Employee> employee, ServerWebExchange exchange) {
		  return create(employeeService, employee.cast(Object.class), exchange)
	        .cast(Employee.class) // Casts the Mono<Object> to Mono<Employee>
	        .map(newEmp ->ResponseEntity.status(HttpStatus.CREATED)
	        .body(newEmp)); // 201 Created for new resource
	  }
	
	  @Override
	  public Mono<ResponseEntity<Employee>> updateEmployee(String employeeKey, Mono<Employee> employee, ServerWebExchange exchange) {
	    return update(employeeService, employeeKey, employee.cast(Object.class), exchange)
	        .cast(Employee.class) // Casts the Mono<Object> to Mono<Employee>
	        .map(ResponseEntity::ok) // 200 OK with the updated Employee
	        .defaultIfEmpty(ResponseEntity.notFound()
	        .build()); // 404 Not Found if the Employee to update doesn't exist
	  }
}
