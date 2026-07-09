package br.com.dled.dledbackend.modules.printtemplates.infrastructure;

import br.com.dled.dledbackend.modules.printtemplates.domain.PrintTemplate;
import br.com.dled.dledbackend.modules.printtemplates.domain.PrintTemplateUsageContext;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrintTemplateRepository extends JpaRepository<PrintTemplate, Long> {
    List<PrintTemplate> findByUsageContextAndActiveTrue(PrintTemplateUsageContext usageContext);
}
