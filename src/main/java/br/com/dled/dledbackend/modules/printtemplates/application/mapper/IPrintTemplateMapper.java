package br.com.dled.dledbackend.modules.printtemplates.application.mapper;

import br.com.dled.dledbackend.modules.printtemplates.application.dto.PrintTemplateDto;
import br.com.dled.dledbackend.modules.printtemplates.application.dto.PrintTemplateListDto;
import br.com.dled.dledbackend.modules.printtemplates.domain.PrintTemplate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IPrintTemplateMapper {
    PrintTemplateDto fromOut(PrintTemplate printTemplate);

    PrintTemplateListDto fromListOut(PrintTemplate printTemplate);
}
