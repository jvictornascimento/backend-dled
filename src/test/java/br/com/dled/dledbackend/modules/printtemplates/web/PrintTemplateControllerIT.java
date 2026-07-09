package br.com.dled.dledbackend.modules.printtemplates.web;

import br.com.dled.dledbackend.modules.printtemplates.domain.PrintTemplate;
import br.com.dled.dledbackend.modules.printtemplates.domain.PrintTemplateUsageContext;
import br.com.dled.dledbackend.support.AbstractWebIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PrintTemplateControllerIT extends AbstractWebIntegrationTest {

    @Test
    void shouldReturnPrintTemplatesListWithoutTemplateJson() throws Exception {
        savePrintTemplate("Etiqueta de mes", PrintTemplateUsageContext.PRINTS_MENU, true);
        savePrintTemplate("Produto tecnico", PrintTemplateUsageContext.PRODUCT, true);

        mockMvc.perform(get("/v1/print-templates")
                        .cookie(authCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Etiqueta de mes"))
                .andExpect(jsonPath("$[0].usageContext").value("PRINTS_MENU"))
                .andExpect(jsonPath("$[0].templateJson").doesNotExist())
                .andExpect(jsonPath("$[1].name").value("Produto tecnico"))
                .andExpect(jsonPath("$[1].usageContext").value("PRODUCT"));
    }

    @Test
    void shouldReturnActivePrintTemplatesByContext() throws Exception {
        savePrintTemplate("Etiqueta ativa", PrintTemplateUsageContext.PRINTS_MENU, true);
        savePrintTemplate("Etiqueta inativa", PrintTemplateUsageContext.PRINTS_MENU, false);
        savePrintTemplate("Produto", PrintTemplateUsageContext.PRODUCT, true);

        mockMvc.perform(get("/v1/print-templates/context/{usageContext}", PrintTemplateUsageContext.PRINTS_MENU)
                        .cookie(authCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Etiqueta ativa"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void shouldReturnPrintTemplateByIdWithTemplateJson() throws Exception {
        PrintTemplate printTemplate = savePrintTemplate("Etiqueta de mes", PrintTemplateUsageContext.MONTH_LABEL, true);

        mockMvc.perform(get("/v1/print-templates/{id}", printTemplate.getId())
                        .cookie(authCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(printTemplate.getId()))
                .andExpect(jsonPath("$.name").value("Etiqueta de mes"))
                .andExpect(jsonPath("$.usageContext").value("MONTH_LABEL"))
                .andExpect(jsonPath("$.templateJson").value(printTemplate.getTemplateJson()))
                .andExpect(jsonPath("$.widthMm").value(25.0))
                .andExpect(jsonPath("$.heightMm").value(33.0))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void shouldReturnNotFoundWhenPrintTemplateDoesNotExist() throws Exception {
        mockMvc.perform(get("/v1/print-templates/{id}", 999L)
                        .cookie(authCookie()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Print template not found"));
    }

    @Test
    void shouldCreatePrintTemplate() throws Exception {
        mockMvc.perform(post("/v1/print-templates")
                        .cookie(authCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Etiqueta de mes",
                                  "description": "Etiqueta mensal 25x33mm",
                                  "usageContext": "PRINTS_MENU",
                                  "active": true,
                                  "templateJson": "{\\"basePdf\\":{\\"width\\":25,\\"height\\":33},\\"schemas\\":[[]]}",
                                  "widthMm": 25,
                                  "heightMm": 33
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Etiqueta de mes"))
                .andExpect(jsonPath("$.description").value("Etiqueta mensal 25x33mm"))
                .andExpect(jsonPath("$.usageContext").value("PRINTS_MENU"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.templateJson").value("{\"basePdf\":{\"width\":25,\"height\":33},\"schemas\":[[]]}"))
                .andExpect(jsonPath("$.widthMm").value(25.0))
                .andExpect(jsonPath("$.heightMm").value(33.0));

        assertThat(printTemplateRepository.findAll())
                .anySatisfy(printTemplate -> {
                    assertThat(printTemplate.getName()).isEqualTo("Etiqueta de mes");
                    assertThat(printTemplate.getUsageContext()).isEqualTo(PrintTemplateUsageContext.PRINTS_MENU);
                    assertThat(printTemplate.getTemplateJson()).contains("\"schemas\"");
                });
    }

    @Test
    void shouldDefaultActiveToTrueWhenCreatingPrintTemplate() throws Exception {
        mockMvc.perform(post("/v1/print-templates")
                        .cookie(authCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Relatorio simples",
                                  "usageContext": "REPORT",
                                  "templateJson": "{\\"basePdf\\":{\\"width\\":210,\\"height\\":297},\\"schemas\\":[[]]}"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldUpdatePrintTemplate() throws Exception {
        PrintTemplate printTemplate = savePrintTemplate("Etiqueta antiga", PrintTemplateUsageContext.PRINTS_MENU, true);

        mockMvc.perform(put("/v1/print-templates/{id}", printTemplate.getId())
                        .cookie(authCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Etiqueta de produto",
                                  "description": "Etiqueta para tela de produto",
                                  "usageContext": "PRODUCT",
                                  "active": false,
                                  "templateJson": "{\\"basePdf\\":{\\"width\\":50,\\"height\\":30},\\"schemas\\":[[]]}",
                                  "widthMm": 50,
                                  "heightMm": 30
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(printTemplate.getId()))
                .andExpect(jsonPath("$.name").value("Etiqueta de produto"))
                .andExpect(jsonPath("$.usageContext").value("PRODUCT"))
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.widthMm").value(50.0))
                .andExpect(jsonPath("$.heightMm").value(30.0));

        PrintTemplate updated = printTemplateRepository.findById(printTemplate.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Etiqueta de produto");
        assertThat(updated.getUsageContext()).isEqualTo(PrintTemplateUsageContext.PRODUCT);
        assertThat(updated.getActive()).isFalse();
        assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldDeletePrintTemplate() throws Exception {
        PrintTemplate printTemplate = savePrintTemplate("Etiqueta de mes", PrintTemplateUsageContext.PRINTS_MENU, true);

        mockMvc.perform(delete("/v1/print-templates/{id}", printTemplate.getId())
                        .cookie(authCookie()))
                .andExpect(status().isNoContent());

        assertThat(printTemplateRepository.findById(printTemplate.getId())).isEmpty();
    }

    @Test
    void shouldValidateRequiredFieldsWhenCreatingPrintTemplate() throws Exception {
        mockMvc.perform(post("/v1/print-templates")
                        .cookie(authCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Sem campos obrigatorios"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
