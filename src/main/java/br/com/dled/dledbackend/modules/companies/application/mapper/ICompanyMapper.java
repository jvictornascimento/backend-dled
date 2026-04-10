package br.com.dled.dledbackend.modules.companies.application.mapper;

import br.com.dled.dledbackend.modules.companies.application.dto.CompanyDto;
import br.com.dled.dledbackend.modules.companies.domain.Company;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICompanyMapper {
    CompanyDto fromOut(Company company);
}
