package br.com.dled.dledbackend.modules.products.web;

import br.com.dled.dledbackend.modules.categories.domain.Category;
import br.com.dled.dledbackend.modules.products.domain.Product;
import br.com.dled.dledbackend.support.AbstractWebIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerIT extends AbstractWebIntegrationTest {

    @Test
    void shouldReturnUnauthorizedWhenApiKeyIsMissing() throws Exception {
        mockMvc.perform(get("/v1/products"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid or missing API key"));
    }

    @Test
    void shouldReturnUnauthorizedWhenApiKeyIsInvalid() throws Exception {
        mockMvc.perform(get("/v1/products")
                        .header(API_KEY_HEADER, "wrong-key"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid or missing API key"));
    }

    @Test
    void shouldReturnActiveProductsList() throws Exception {
        Category category = saveRootCategory("Drivers");
        saveProduct("Driver 24W", category);

        mockMvc.perform(get("/v1/products")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Driver 24W"))
                .andExpect(jsonPath("$[0].categories[0].name").value("Drivers"));
    }

    @Test
    void shouldReturnProductDetailById() throws Exception {
        Category category = saveRootCategory("Fitas");
        Product product = saveProduct("Fita LED", category);

        mockMvc.perform(get("/v1/products/{id}", product.getId())
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("Fita LED"))
                .andExpect(jsonPath("$.descricao").value("Product description"))
                .andExpect(jsonPath("$.categories[0].name").value("Fitas"));
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        mockMvc.perform(get("/v1/products/{id}", 999L)
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Product not found"));
    }

    @Test
    void shouldReturnMethodNotAllowedForUnsupportedRequest() throws Exception {
        mockMvc.perform(post("/v1/products")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }
}
