package com.hst.materialmgmt.controller.company;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.hst.api.EmployeeApi;
import com.hst.api.model.Employee;
import com.hst.materialmgmt.controller.BaseController;
import com.hst.materialmgmt.service.company.EmployeeService;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "Employee API", description = "Endpoints for employee operations")
public class EmployeeController extends BaseController implements EmployeeApi {

    @Autowired
    private EmployeeService employeeService;

    @Override
    public Mono<ResponseEntity<Void>> deleteEmployee(
            String employeeKey, ServerWebExchange exchange) {
        return delete(employeeService, employeeKey, exchange);
    }

    // Satisfies interface — returns single (unused)
    @Override
    public Mono<ResponseEntity<Employee>> getAllEmployees(
            ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok((Employee) null));
    }

    // Returns full list
    @GetMapping("/employees/all")
    public Mono<ResponseEntity<List<Employee>>> getAllEmployeesList(
            ServerWebExchange exchange) {
        return findAll(employeeService, exchange)
                .cast(Employee.class)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(List.of()));
    }

    @Override
    public Mono<ResponseEntity<Employee>> getEmployeeById(
            String employeeKey, ServerWebExchange exchange) {
        return findByKey(employeeService, employeeKey, exchange)
                .cast(Employee.class)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Employee>> createEmployee(
            Mono<Employee> employee, ServerWebExchange exchange) {
        return create(employeeService, employee.cast(Object.class), exchange)
                .cast(Employee.class)
                .map(e -> ResponseEntity.status(HttpStatus.CREATED).body(e));
    }

    @Override
    public Mono<ResponseEntity<Employee>> updateEmployee(
            String employeeKey, Mono<Employee> employee,
            ServerWebExchange exchange) {
        return update(employeeService, employeeKey,
                employee.cast(Object.class), exchange)
                .cast(Employee.class)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
