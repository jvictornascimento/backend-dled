package br.com.dled.dledbackend.modules.companies.web;

import br.com.dled.dledbackend.modules.companies.domain.Company;
import br.com.dled.dledbackend.modules.companies.domain.CompanyType;
import br.com.dled.dledbackend.support.AbstractWebIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CompanyControllerIT extends AbstractWebIntegrationTest {

    @Test
    void shouldReturnCompaniesList() throws Exception {
        saveCompany("DLED", "DLED Lighting", CompanyType.OWN);
        saveCompany("ACME", "ACME Supplies", CompanyType.SUPPLIER);

        mockMvc.perform(get("/v1/companies")
                        .cookie(authCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shortName").value("DLED"))
                .andExpect(jsonPath("$[0].type").value("OWN"))
                .andExpect(jsonPath("$[1].shortName").value("ACME"))
                .andExpect(jsonPath("$[1].type").value("SUPPLIER"));
    }

    @Test
    void shouldReturnCompanyById() throws Exception {
        Company company = saveCompany("DLED", "DLED Lighting", CompanyType.OWN);

        mockMvc.perform(get("/v1/companies/{id}", company.getId())
                        .cookie(authCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(company.getId()))
                .andExpect(jsonPath("$.shortName").value("DLED"))
                .andExpect(jsonPath("$.fullName").value("DLED Lighting"))
                .andExpect(jsonPath("$.type").value("OWN"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldReturnNotFoundWhenCompanyDoesNotExist() throws Exception {
        mockMvc.perform(get("/v1/companies/{id}", 999L)
                        .cookie(authCookie()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Company not found"));
    }

    @Test
    void shouldReturnMethodNotAllowedForUnsupportedRequest() throws Exception {
        mockMvc.perform(patch("/v1/companies")
                        .cookie(authCookie()))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.error").value("Method Not Allowed"));
    }

    @Test
    void shouldCreateCompany() throws Exception {
        mockMvc.perform(post("/v1/companies")
                        .cookie(authCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "shortName": "DLED",
                                  "fullName": "DLED Lighting",
                                  "type": "OWN"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.shortName").value("DLED"))
                .andExpect(jsonPath("$.fullName").value("DLED Lighting"))
                .andExpect(jsonPath("$.type").value("OWN"))
                .andExpect(jsonPath("$.createdAt").exists());

        assertThat(companyRepository.findAll())
                .anySatisfy(company -> {
                    assertThat(company.getShortName()).isEqualTo("DLED");
                    assertThat(company.getFullName()).isEqualTo("DLED Lighting");
                    assertThat(company.getType()).isEqualTo(CompanyType.OWN);
                });
    }

    @Test
    void shouldUpdateCompany() throws Exception {
        Company company = saveCompany("ACME", "ACME Supplies", CompanyType.SUPPLIER);

        mockMvc.perform(put("/v1/companies/{id}", company.getId())
                        .cookie(authCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "shortName": "DLED",
                                  "fullName": "DLED Industria de Iluminacao",
                                  "type": "OWN"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(company.getId()))
                .andExpect(jsonPath("$.shortName").value("DLED"))
                .andExpect(jsonPath("$.fullName").value("DLED Industria de Iluminacao"))
                .andExpect(jsonPath("$.type").value("OWN"));

        Company updated = companyRepository.findById(company.getId()).orElseThrow();
        assertThat(updated.getShortName()).isEqualTo("DLED");
        assertThat(updated.getFullName()).isEqualTo("DLED Industria de Iluminacao");
        assertThat(updated.getType()).isEqualTo(CompanyType.OWN);
    }

    @Test
    void shouldDeleteCompany() throws Exception {
        Company company = saveCompany("ACME", "ACME Supplies", CompanyType.SUPPLIER);

        mockMvc.perform(delete("/v1/companies/{id}", company.getId())
                        .cookie(authCookie()))
                .andExpect(status().isNoContent());

        assertThat(companyRepository.findById(company.getId())).isEmpty();
    }
}
