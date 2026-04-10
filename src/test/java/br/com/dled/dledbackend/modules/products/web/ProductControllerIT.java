package br.com.dled.dledbackend.modules.products.web;

import br.com.dled.dledbackend.modules.categories.domain.Category;
import br.com.dled.dledbackend.modules.products.domain.Product;
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
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"))
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
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.descricao").value("Product description"))
                .andExpect(jsonPath("$.restricoesDeUso").value("Evitar ambientes com maresia."))
                .andExpect(jsonPath("$.recomendacoesDeUso").value("Indicado para ambientes internos."))
                .andExpect(jsonPath("$.observacoesEspeciais").value("Garantia de 12 meses."))
                .andExpect(jsonPath("$.observacoesInternas").doesNotExist())
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
        mockMvc.perform(patch("/v1/products")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    void shouldCreateProduct() throws Exception {
        Category category = saveRootCategory("Drivers");

        mockMvc.perform(post("/v1/products")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Driver 60W",
                                  "codigoRusso": 321,
                                  "codigoMali": 654,
                                  "categoryIds": [%d],
                                  "status": "AVAILABLE",
                                  "descricao": "Driver para fitas LED",
                                  "restricoesDeUso": "Nao usar em area externa.",
                                  "recomendacoesDeUso": "Usar em gesso e marcenaria.",
                                  "observacoesEspeciais": "Produto com lote especial.",
                                  "ip": 65,
                                  "amper": 5,
                                  "watts": 60,
                                  "gtin": 7891234567000,
                                  "volt": 24,
                                  "imgUrl": "driver-60w.png",
                                  "price": 149.9,
                                  "iconUrl": "driver-60w-icon.png",
                                  "temperaturaDeCor": "3000K",
                                  "ledsPorMetro": 120,
                                  "tipoLed": "SMD",
                                  "fluxoLuminoso": "2400lm",
                                  "indiceDeReproducaoDeCor": "90",
                                  "quantidePorRolo": 5,
                                  "sessaoDeCorte": 10,
                                  "espessura": 2,
                                  "blindada": true,
                                  "dimensao": "5m",
                                  "active": true
                                }
                                """.formatted(category.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Driver 60W"))
                .andExpect(jsonPath("$.codigoRusso").value(321))
                .andExpect(jsonPath("$.codigoMali").value(654))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.descricao").value("Driver para fitas LED"))
                .andExpect(jsonPath("$.restricoesDeUso").value("Nao usar em area externa."))
                .andExpect(jsonPath("$.recomendacoesDeUso").value("Usar em gesso e marcenaria."))
                .andExpect(jsonPath("$.observacoesEspeciais").value("Produto com lote especial."))
                .andExpect(jsonPath("$.observacoesInternas").doesNotExist())
                .andExpect(jsonPath("$.categories[0].name").value("Drivers"));

        assertThat(productRepository.findAll())
                .anySatisfy(product -> {
                    assertThat(product.getName()).isEqualTo("Driver 60W");
                    assertThat(product.getCodigoRusso()).isEqualTo(321);
                    assertThat(product.getStatus().name()).isEqualTo("AVAILABLE");
                    assertThat(product.getRestricoesDeUso()).isEqualTo("Nao usar em area externa.");
                    assertThat(product.getRecomendacoesDeUso()).isEqualTo("Usar em gesso e marcenaria.");
                    assertThat(product.getObservacoesEspeciais()).isEqualTo("Produto com lote especial.");
                    assertThat(product.getCategories()).extracting(Category::getId).contains(category.getId());
                });
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        Category originalCategory = saveRootCategory("Drivers");
        Category updatedCategory = saveRootCategory("Perfis");
        Product product = saveProduct("Driver 24W", originalCategory);

        mockMvc.perform(put("/v1/products/{id}", product.getId())
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Perfil 12W",
                                  "codigoRusso": 777,
                                  "codigoMali": 888,
                                  "categoryIds": [%d],
                                  "status": "ON_REQUEST",
                                  "descricao": "Perfil atualizado",
                                  "restricoesDeUso": "Nao instalar em local umido.",
                                  "recomendacoesDeUso": "Aplicar com fonte estabilizada.",
                                  "observacoesEspeciais": "Revisar lote na expedicao.",
                                  "ip": 20,
                                  "amper": 2,
                                  "watts": 12,
                                  "gtin": 7891234567999,
                                  "volt": 12,
                                  "imgUrl": "perfil-12w.png",
                                  "price": 89.9,
                                  "iconUrl": "perfil-12w-icon.png",
                                  "temperaturaDeCor": "4000K",
                                  "ledsPorMetro": 60,
                                  "tipoLed": "COB",
                                  "fluxoLuminoso": "900lm",
                                  "indiceDeReproducaoDeCor": "95",
                                  "quantidePorRolo": 3,
                                  "sessaoDeCorte": 5,
                                  "espessura": 1,
                                  "blindada": false,
                                  "dimensao": "3m",
                                  "active": false
                                }
                                """.formatted(updatedCategory.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("Perfil 12W"))
                .andExpect(jsonPath("$.codigoRusso").value(777))
                .andExpect(jsonPath("$.codigoMali").value(888))
                .andExpect(jsonPath("$.status").value("ON_REQUEST"))
                .andExpect(jsonPath("$.descricao").value("Perfil atualizado"))
                .andExpect(jsonPath("$.restricoesDeUso").value("Nao instalar em local umido."))
                .andExpect(jsonPath("$.recomendacoesDeUso").value("Aplicar com fonte estabilizada."))
                .andExpect(jsonPath("$.observacoesEspeciais").value("Revisar lote na expedicao."))
                .andExpect(jsonPath("$.observacoesInternas").doesNotExist())
                .andExpect(jsonPath("$.categories[0].name").value("Perfis"))
                .andExpect(jsonPath("$.active").value(false));

        Product updated = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Perfil 12W");
        assertThat(updated.getCodigoRusso()).isEqualTo(777);
        assertThat(updated.getStatus().name()).isEqualTo("ON_REQUEST");
        assertThat(updated.getRestricoesDeUso()).isEqualTo("Nao instalar em local umido.");
        assertThat(updated.getRecomendacoesDeUso()).isEqualTo("Aplicar com fonte estabilizada.");
        assertThat(updated.getObservacoesEspeciais()).isEqualTo("Revisar lote na expedicao.");
        assertThat(updated.getCategories()).extracting(Category::getId).containsExactly(updatedCategory.getId());
        assertThat(updated.isActive()).isFalse();
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        Category category = saveRootCategory("Drivers");
        Product product = saveProduct("Driver 24W", category);

        mockMvc.perform(delete("/v1/products/{id}", product.getId())
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isNoContent());

        assertThat(productRepository.findById(product.getId())).isEmpty();
    }
}
