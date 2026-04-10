package br.com.dled.dledbackend.modules.companies.application;

import br.com.dled.dledbackend.modules.companies.application.dto.CompanyDto;
import br.com.dled.dledbackend.modules.companies.application.dto.CompanyUpsertDto;
import br.com.dled.dledbackend.modules.companies.application.exception.CompanyNotFoundException;
import br.com.dled.dledbackend.modules.companies.application.mapper.ICompanyMapper;
import br.com.dled.dledbackend.modules.companies.domain.Company;
import br.com.dled.dledbackend.modules.companies.domain.CompanyType;
import br.com.dled.dledbackend.modules.companies.infrastructure.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository repository;

    @Mock
    private ICompanyMapper mapper;

    @InjectMocks
    private CompanyServiceImpl service;

    @Test
    void shouldReturnMappedCompanies() {
        Company company = createCompany(1L, "DLED", "DLED Lighting", CompanyType.OWN);
        CompanyDto dto = createDto(1L, "DLED", "DLED Lighting", CompanyType.OWN);

        when(repository.findAll()).thenReturn(List.of(company));
        when(mapper.fromOut(company)).thenReturn(dto);

        List<CompanyDto> result = service.getAll();

        assertEquals(List.of(dto), result);
        verify(mapper).fromOut(company);
    }

    @Test
    void shouldReturnCompanyById() {
        Company company = createCompany(1L, "ACME", "ACME Supplies", CompanyType.SUPPLIER);
        CompanyDto dto = createDto(1L, "ACME", "ACME Supplies", CompanyType.SUPPLIER);

        when(repository.findById(1L)).thenReturn(Optional.of(company));
        when(mapper.fromOut(company)).thenReturn(dto);

        CompanyDto result = service.getById(1L);

        assertSame(dto, result);
    }

    @Test
    void shouldCreateCompany() {
        CompanyUpsertDto input = new CompanyUpsertDto("DLED", "DLED Lighting", CompanyType.OWN);
        Company saved = createCompany(1L, "DLED", "DLED Lighting", CompanyType.OWN);
        CompanyDto dto = createDto(1L, "DLED", "DLED Lighting", CompanyType.OWN);

        when(repository.save(org.mockito.ArgumentMatchers.any(Company.class))).thenReturn(saved);
        when(mapper.fromOut(saved)).thenReturn(dto);

        CompanyDto result = service.create(input);

        assertSame(dto, result);
    }

    @Test
    void shouldUpdateCompany() {
        Company existing = createCompany(1L, "ACME", "ACME Supplies", CompanyType.SUPPLIER);
        CompanyDto dto = createDto(1L, "DLED", "DLED Lighting", CompanyType.OWN);
        CompanyUpsertDto input = new CompanyUpsertDto("DLED", "DLED Lighting", CompanyType.OWN);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.fromOut(existing)).thenReturn(dto);

        CompanyDto result = service.update(1L, input);

        assertSame(dto, result);
        assertEquals("DLED", existing.getShortName());
        assertEquals("DLED Lighting", existing.getFullName());
        assertEquals(CompanyType.OWN, existing.getType());
    }

    @Test
    void shouldDeleteCompany() {
        Company company = createCompany(1L, "ACME", "ACME Supplies", CompanyType.SUPPLIER);

        when(repository.findById(1L)).thenReturn(Optional.of(company));

        service.delete(1L);

        verify(repository).delete(company);
    }

    @Test
    void shouldThrowWhenCompanyDoesNotExist() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () -> service.getById(1L));
        assertThrows(CompanyNotFoundException.class, () -> service.update(1L, new CompanyUpsertDto("DLED", "DLED Lighting", CompanyType.OWN)));
        assertThrows(CompanyNotFoundException.class, () -> service.delete(1L));
    }

    private Company createCompany(Long id, String shortName, String fullName, CompanyType type) {
        Company company = new Company();
        company.setId(id);
        company.setShortName(shortName);
        company.setFullName(fullName);
        company.setType(type);
        company.setCreatedAt(LocalDateTime.of(2026, 4, 10, 8, 0));
        return company;
    }

    private CompanyDto createDto(Long id, String shortName, String fullName, CompanyType type) {
        return new CompanyDto(id, shortName, fullName, type, LocalDateTime.of(2026, 4, 10, 8, 0));
    }
}
