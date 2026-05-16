package com.hst.materialmgmt.controller.company;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import com.hst.api.DepartmentApi;
import com.hst.api.model.Department;
import com.hst.materialmgmt.controller.BaseController;
import com.hst.materialmgmt.service.company.DepartmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt")
@Tag(name = "Department API", description = "Endpoints for department operations")
public class DepartmentController extends BaseController implements DepartmentApi {

    @Autowired
    private DepartmentService departmentService;

    @Override
    public Mono<ResponseEntity<Void>> deleteDepartment(String departmentKey, ServerWebExchange exchange) {
        return delete(departmentService, departmentKey, exchange);
    }

    @Override
    public Mono<ResponseEntity<Department>> getAllDepartments(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok((Department) null));
    }

    @GetMapping("/departments/all")
    public Mono<ResponseEntity<List<Department>>> getAllDepartmentsList(ServerWebExchange exchange) {
        return findAll(departmentService, exchange)
                .cast(Department.class)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(List.of()));
    }

    @Override
    public Mono<ResponseEntity<Department>> getDepartmentById(String departmentKey, ServerWebExchange exchange) {
        return findByKey(departmentService, departmentKey, exchange)
                .cast(Department.class)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Department>> createDepartment(Mono<Department> department, ServerWebExchange exchange) {
        return create(departmentService, department.cast(Object.class), exchange)
                .cast(Department.class)
                .map(d -> ResponseEntity.status(HttpStatus.CREATED).body(d));
    }

    @Override
    public Mono<ResponseEntity<Department>> updateDepartment(String departmentKey, Mono<Department> department, ServerWebExchange exchange) {
        return update(departmentService, departmentKey, department.cast(Object.class), exchange)
                .cast(Department.class)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
