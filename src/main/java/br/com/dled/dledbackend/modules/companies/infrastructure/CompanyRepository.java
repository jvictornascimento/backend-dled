package br.com.dled.dledbackend.modules.companies.infrastructure;

import br.com.dled.dledbackend.modules.companies.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
