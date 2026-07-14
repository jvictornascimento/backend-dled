package br.com.dled.dledbackend.modules.companies.application;

import br.com.dled.dledbackend.modules.companies.application.dto.CompanyDto;
import br.com.dled.dledbackend.modules.companies.application.dto.CompanyUpsertDto;

import java.util.List;

public interface ICompanyService {
    List<CompanyDto> getAll();
    CompanyDto getById(Long companyId);
    CompanyDto create(CompanyUpsertDto input);
    CompanyDto update(Long companyId, CompanyUpsertDto input);
    void delete(Long companyId);
}
