package com.hst.materialmgmt.service.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.api.model.Employee;
import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.company.EmployeeMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.company.EmployeeRepository;
import com.hst.materialmgmt.service.SingleEntityBaseService;

import reactor.core.publisher.Mono;

@Service
public class EmployeeService extends SingleEntityBaseService {

    @Autowired private EmployeeRepository    employeeRepository;
    @Autowired private EmployeeMapper        employeeMapper;
    @Autowired private EmployeeCodeGenerator codeGenerator;

    @Override
    protected BaseMapper getMapper() { return employeeMapper; }

    @Override
    protected ParentRepositoryImpl getMasterRepository() {
        return employeeRepository;
    }

    @Override
    public Mono<Object> createFullHierarchy(Mono<Object> parentMono) {
        if (parentMono == null)
            return Mono.error(new IllegalArgumentException("Employee cannot be null"));

        return parentMono.flatMap(model -> {
            Employee emp = (Employee) model;
            return codeGenerator.nextEmployeeCode()
                    .flatMap(code -> {
                        emp.setEmployeeId(code);
                        return super.createFullHierarchy(Mono.just((Object) emp));
                    });
        });
    }
}
