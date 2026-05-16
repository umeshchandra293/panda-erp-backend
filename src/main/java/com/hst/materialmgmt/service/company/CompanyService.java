package com.hst.materialmgmt.service.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.api.model.Company;
import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.company.CompanyMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.company.CompanyRepository;
import com.hst.materialmgmt.service.SingleEntityBaseService;

import reactor.core.publisher.Mono;

@Service
public class CompanyService extends SingleEntityBaseService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private CompanyCodeGenerator codeGenerator;

    @Override
    protected BaseMapper getMapper() {
        return companyMapper;
    }

    @Override
    protected ParentRepositoryImpl getMasterRepository() {
        return companyRepository;
    }

    /**
     * Override create to inject a generated company_id before saving.
     */
    @Override
    public Mono<Object> createFullHierarchy(Mono<Object> parentMono) {
        if (parentMono == null)
            return Mono.error(new IllegalArgumentException("Company cannot be null"));

        Mono<Object> codedMono = parentMono.flatMap(model -> {
            Company company = (Company) model;
            return codeGenerator.nextCompanyCode()
                    .map(code -> {
                        company.setCompanyId(code);
                        return (Object) company;
                    });
        });

        return super.createFullHierarchy(codedMono);
    }
}
