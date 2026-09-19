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
                                  "caixa": "CX-01",
                                  "woodType": "MDF",
                                  "finish": "Natural",
                                  "price": 49.9,
                                  "thicknessMm": 18.0,
                                  "widthMm": 30.0,
                                  "heightMm": 12.5,
                                  "lengthMm": 40.0,
                                  "weightKg": 2.2,
                                  "categoryIds": [%d],
                                  "active": true
                                }
                                """.formatted(category.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Caixa MDF"))
                .andExpect(jsonPath("$.caixa").value("CX-01"))
                .andExpect(jsonPath("$.woodType").value("MDF"))
                .andExpect(jsonPath("$.finish").value("Natural"))
                .andExpect(jsonPath("$.price").value(49.9))
                .andExpect(jsonPath("$.thicknessMm").value(18.0))
                .andExpect(jsonPath("$.widthMm").value(30.0))
                .andExpect(jsonPath("$.heightMm").value(12.5))
                .andExpect(jsonPath("$.lengthMm").value(40.0))
                .andExpect(jsonPath("$.weightKg").value(2.2));
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

    @Test
    void shouldRejectInvalidWoodProductDimensions() throws Exception {
        mockMvc.perform(post("/v1/wood/products")
                        .cookie(employAuthCookie())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Caixa invalida",
                                  "woodType": "MDF",
                                  "finish": "Natural",
                                  "thicknessMm": -1,
                                  "active": true
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
