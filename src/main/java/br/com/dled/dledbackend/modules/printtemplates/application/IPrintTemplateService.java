package br.com.dled.dledbackend.modules.printtemplates.application;

import br.com.dled.dledbackend.modules.printtemplates.application.dto.PrintTemplateCreateDto;
import br.com.dled.dledbackend.modules.printtemplates.application.dto.PrintTemplateDto;
import br.com.dled.dledbackend.modules.printtemplates.application.dto.PrintTemplateListDto;
import br.com.dled.dledbackend.modules.printtemplates.application.dto.PrintTemplateUpdateDto;
import br.com.dled.dledbackend.modules.printtemplates.domain.PrintTemplateUsageContext;

import java.util.List;

public interface IPrintTemplateService {
    List<PrintTemplateListDto> getAll();

    List<PrintTemplateListDto> getActiveByUsageContext(PrintTemplateUsageContext usageContext);

    PrintTemplateDto getById(Long printTemplateId);

    PrintTemplateDto create(PrintTemplateCreateDto input);

    PrintTemplateDto update(Long printTemplateId, PrintTemplateUpdateDto input);

    void delete(Long printTemplateId);
}
