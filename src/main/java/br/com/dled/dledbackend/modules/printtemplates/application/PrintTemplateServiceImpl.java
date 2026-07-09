package br.com.dled.dledbackend.modules.printtemplates.application;

import br.com.dled.dledbackend.modules.printtemplates.application.dto.PrintTemplateCreateDto;
import br.com.dled.dledbackend.modules.printtemplates.application.dto.PrintTemplateDto;
import br.com.dled.dledbackend.modules.printtemplates.application.dto.PrintTemplateListDto;
import br.com.dled.dledbackend.modules.printtemplates.application.dto.PrintTemplateUpdateDto;
import br.com.dled.dledbackend.modules.printtemplates.application.exception.PrintTemplateNotFoundException;
import br.com.dled.dledbackend.modules.printtemplates.application.mapper.IPrintTemplateMapper;
import br.com.dled.dledbackend.modules.printtemplates.domain.PrintTemplate;
import br.com.dled.dledbackend.modules.printtemplates.domain.PrintTemplateUsageContext;
import br.com.dled.dledbackend.modules.printtemplates.infrastructure.PrintTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.PRINT_TEMPLATE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PrintTemplateServiceImpl implements IPrintTemplateService {
    private final PrintTemplateRepository repository;
    private final IPrintTemplateMapper mapper;

    @Override
    public List<PrintTemplateListDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::fromListOut)
                .toList();
    }

    @Override
    public List<PrintTemplateListDto> getActiveByUsageContext(PrintTemplateUsageContext usageContext) {
        return repository.findByUsageContextAndActiveTrue(usageContext).stream()
                .map(mapper::fromListOut)
                .toList();
    }

    @Override
    public PrintTemplateDto getById(Long printTemplateId) {
        return mapper.fromOut(findById(printTemplateId));
    }

    @Override
    public PrintTemplateDto create(PrintTemplateCreateDto input) {
        PrintTemplate printTemplate = new PrintTemplate();
        applyInput(printTemplate, input.name(), input.description(), input.usageContext(), input.active(),
                input.templateJson(), input.widthMm(), input.heightMm());
        return mapper.fromOut(repository.save(printTemplate));
    }

    @Override
    public PrintTemplateDto update(Long printTemplateId, PrintTemplateUpdateDto input) {
        PrintTemplate printTemplate = findById(printTemplateId);
        applyInput(printTemplate, input.name(), input.description(), input.usageContext(), input.active(),
                input.templateJson(), input.widthMm(), input.heightMm());
        return mapper.fromOut(repository.save(printTemplate));
    }

    @Override
    public void delete(Long printTemplateId) {
        repository.delete(findById(printTemplateId));
    }

    private PrintTemplate findById(Long printTemplateId) {
        return repository.findById(printTemplateId)
                .orElseThrow(() -> new PrintTemplateNotFoundException(PRINT_TEMPLATE_NOT_FOUND.getMassage()));
    }

    private void applyInput(PrintTemplate printTemplate, String name, String description,
                            PrintTemplateUsageContext usageContext, Boolean active, String templateJson,
                            Double widthMm, Double heightMm) {
        printTemplate.setName(name);
        printTemplate.setDescription(description);
        printTemplate.setUsageContext(usageContext);
        printTemplate.setActive(active == null || active);
        printTemplate.setTemplateJson(templateJson);
        printTemplate.setWidthMm(widthMm);
        printTemplate.setHeightMm(heightMm);
    }
}
