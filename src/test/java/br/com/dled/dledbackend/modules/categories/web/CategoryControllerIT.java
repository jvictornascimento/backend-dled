package br.com.dled.dledbackend.modules.categories.web;

import br.com.dled.dledbackend.modules.categories.domain.Category;
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

    @Test
    void shouldCreateCategory() throws Exception {
        Category parent = saveRootCategory("Pai");

        mockMvc.perform(post("/v1/categories")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Nova Categoria",
                                  "imgUrl": "nova-categoria.png",
                                  "active": true,
                                  "parentId": %d
                                }
                                """.formatted(parent.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Nova Categoria"))
                .andExpect(jsonPath("$.imgUrl").value("nova-categoria.png"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.parentId").value(parent.getId()));

        assertThat(categoryRepository.findAll())
                .anySatisfy(category -> {
                    assertThat(category.getName()).isEqualTo("Nova Categoria");
                    assertThat(category.isActive()).isTrue();
                    assertThat(category.getParent()).isNotNull();
                    assertThat(category.getParent().getId()).isEqualTo(parent.getId());
                });
    }

    @Test
    void shouldUpdateCategory() throws Exception {
        Category parent = saveRootCategory("Pai");
        Category category = saveChildCategory("Categoria Antiga", parent);

        mockMvc.perform(put("/v1/categories/{id}", category.getId())
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Categoria Atualizada",
                                  "imgUrl": "categoria-atualizada.png",
                                  "active": false,
                                  "parentId": null
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value("Categoria Atualizada"))
                .andExpect(jsonPath("$.imgUrl").value("categoria-atualizada.png"))
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.parentId").doesNotExist());

        Category updated = categoryRepository.findById(category.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Categoria Atualizada");
        assertThat(updated.getImgUrl()).isEqualTo("categoria-atualizada.png");
        assertThat(updated.isActive()).isFalse();
        assertThat(updated.getParent()).isNull();
    }

    @Test
    void shouldDeleteCategory() throws Exception {
        Category category = saveRootCategory("Categoria Removida");

        mockMvc.perform(delete("/v1/categories/{id}", category.getId())
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isNoContent());

        assertThat(categoryRepository.findById(category.getId())).isEmpty();
    }
}
