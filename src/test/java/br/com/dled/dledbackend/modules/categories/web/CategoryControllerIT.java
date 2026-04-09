package br.com.dled.dledbackend.modules.categories.web;

import br.com.dled.dledbackend.modules.categories.domain.Category;
import br.com.dled.dledbackend.support.AbstractWebIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerIT extends AbstractWebIntegrationTest {

    @Test
    void shouldReturnRootCategories() throws Exception {
        saveRootCategory("Drivers");
        saveRootCategory("Perfis");

        mockMvc.perform(get("/v1/categories/root")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Drivers"))
                .andExpect(jsonPath("$[1].name").value("Perfis"));
    }

    @Test
    void shouldReturnCategoryTree() throws Exception {
        Category root = saveRootCategory("Fitas");
        saveChildCategory("Acessorios", root);

        mockMvc.perform(get("/v1/categories/tree")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Fitas"))
                .andExpect(jsonPath("$[0].children").isEmpty())
                .andExpect(jsonPath("$[1].name").value("Acessorios"))
                .andExpect(jsonPath("$[1].children").isEmpty());
    }

    @Test
    void shouldReturnCategoryById() throws Exception {
        Category category = saveRootCategory("Fontes");

        mockMvc.perform(get("/v1/categories/{id}", category.getId())
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value("Fontes"))
                .andExpect(jsonPath("$.parentId").doesNotExist());
    }

    @Test
    void shouldReturnNotFoundWhenCategoryDoesNotExist() throws Exception {
        mockMvc.perform(get("/v1/categories/{id}", 999L)
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }

    @Test
    void shouldReturnMethodNotAllowedForUnsupportedRequest() throws Exception {
        mockMvc.perform(post("/v1/categories/root")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }
}
