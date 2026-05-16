package com.hst.materialmgmt.service.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.api.model.Department;
import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.company.DepartmentMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.company.DepartmentRepository;
import com.hst.materialmgmt.service.SingleEntityBaseService;

import reactor.core.publisher.Mono;

@Service
public class DepartmentService extends SingleEntityBaseService {

    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private DepartmentMapper     departmentMapper;
    @Autowired private DepartmentCodeGenerator codeGenerator;

    @Override
    protected BaseMapper getMapper() { return departmentMapper; }

    @Override
    protected ParentRepositoryImpl getMasterRepository() {
        return departmentRepository;
    }

    @Override
    public Mono<Object> createFullHierarchy(Mono<Object> parentMono) {
        if (parentMono == null)
            return Mono.error(new IllegalArgumentException("Department cannot be null"));

        Mono<Object> codedMono = parentMono.flatMap(model -> {
            Department dept = (Department) model;
            return codeGenerator.nextDepartmentCode()
                    .map(code -> {
                        dept.setDepartmentId(code);
                        return (Object) dept;
                    });
        });

        return super.createFullHierarchy(codedMono);
    }
}
