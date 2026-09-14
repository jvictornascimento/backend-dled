package br.com.dled.dledbackend.modules.wood.web;

import br.com.dled.dledbackend.modules.wood.domain.WoodCategory;
import br.com.dled.dledbackend.support.AbstractWebIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WoodProductControllerIT extends AbstractWebIntegrationTest {

    @Test
    void shouldAllowEmployToCreateWoodProduct() throws Exception {
        WoodCategory category = saveWoodRootCategory("MDF");

        mockMvc.perform(post("/v1/wood/products")
                        .cookie(employAuthCookie())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Caixa MDF",
                                  "description": "Caixa para fita LED",
                                  "price": 49.9,
                                  "categoryIds": [%d],
                                  "active": true
                                }
                                """.formatted(category.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Caixa MDF"));
    }

    @Test
    void shouldForbidRegularUserFromCreatingWoodProduct() throws Exception {
        WoodCategory category = saveWoodRootCategory("MDF");

        mockMvc.perform(post("/v1/wood/products")
                        .cookie(authCookie())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Caixa bloqueada",
                                  "description": "Caixa para fita LED",
                                  "price": 49.9,
                                  "categoryIds": [%d],
                                  "active": true
                                }
                                """.formatted(category.getId())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }
}
