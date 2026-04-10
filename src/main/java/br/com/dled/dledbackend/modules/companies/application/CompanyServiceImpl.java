package br.com.dled.dledbackend.modules.companies.application;

import br.com.dled.dledbackend.modules.companies.application.dto.CompanyDto;
import br.com.dled.dledbackend.modules.companies.application.dto.CompanyUpsertDto;
import br.com.dled.dledbackend.modules.companies.application.exception.CompanyNotFoundException;
import br.com.dled.dledbackend.modules.companies.application.mapper.ICompanyMapper;
import br.com.dled.dledbackend.modules.companies.domain.Company;
import br.com.dled.dledbackend.modules.companies.infrastructure.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.COMPANY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements ICompanyService {
    private final CompanyRepository repository;
    private final ICompanyMapper mapper;

    @Override
    public List<CompanyDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::fromOut)
                .toList();
    }

    @Override
    public CompanyDto getById(Long companyId) {
        return mapper.fromOut(findById(companyId));
    }

    @Override
    public CompanyDto create(CompanyUpsertDto input) {
        Company company = new Company();
        applyInput(company, input);
        return mapper.fromOut(repository.save(company));
    }

    @Override
    public CompanyDto update(Long companyId, CompanyUpsertDto input) {
        Company company = findById(companyId);
        applyInput(company, input);
        return mapper.fromOut(repository.save(company));
    }

    @Override
    public void delete(Long companyId) {
        repository.delete(findById(companyId));
    }

    private Company findById(Long companyId) {
        return repository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(COMPANY_NOT_FOUND.getMassage()));
    }

    private void applyInput(Company company, CompanyUpsertDto input) {
        company.setShortName(input.shortName());
        company.setFullName(input.fullName());
        company.setType(input.type());
    }
}
