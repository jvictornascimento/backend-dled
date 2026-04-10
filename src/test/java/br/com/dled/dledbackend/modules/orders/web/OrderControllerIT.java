package br.com.dled.dledbackend.modules.orders.web;

import br.com.dled.dledbackend.modules.categories.domain.Category;
import br.com.dled.dledbackend.modules.companies.domain.Company;
import br.com.dled.dledbackend.modules.companies.domain.CompanyType;
import br.com.dled.dledbackend.modules.orders.domain.Order;
import br.com.dled.dledbackend.modules.products.domain.Product;
import br.com.dled.dledbackend.support.AbstractWebIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerIT extends AbstractWebIntegrationTest {

    @Test
    void shouldReturnOrdersList() throws Exception {
        Category category = saveRootCategory("Drivers");
        Product firstProduct = saveProduct("Driver 24W", category);
        Product secondProduct = saveProduct("Driver 48W", category);
        Company company = saveCompany("ACME", "ACME Supplies", CompanyType.SUPPLIER);
        saveOrder(LocalDate.of(2026, 4, 10), "L-2026-001", company, firstProduct, secondProduct);

        mockMvc.perform(get("/v1/orders")
                        .cookie(authCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lot").value("L-2026-001"))
                .andExpect(jsonPath("$[0].purchaseDate").value("2026-04-10"))
                .andExpect(jsonPath("$[0].company.shortName").value("ACME"))
                .andExpect(jsonPath("$[0].products.length()").value(2));
    }

    @Test
    void shouldReturnOrderById() throws Exception {
        Category category = saveRootCategory("Perfis");
        Product product = saveProduct("Perfil 12W", category);
        Company company = saveCompany("DLED", "DLED Lighting", CompanyType.OWN);
        Order order = saveOrder(LocalDate.of(2026, 4, 9), "L-2026-002", company, product);

        mockMvc.perform(get("/v1/orders/{id}", order.getId())
                        .cookie(authCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.lot").value("L-2026-002"))
                .andExpect(jsonPath("$.purchaseDate").value("2026-04-09"))
                .andExpect(jsonPath("$.company.type").value("OWN"))
                .andExpect(jsonPath("$.products[0].name").value("Perfil 12W"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        mockMvc.perform(get("/v1/orders/{id}", 999L)
                        .cookie(authCookie()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Order not found"));
    }

    @Test
    void shouldReturnMethodNotAllowedForUnsupportedRequest() throws Exception {
        mockMvc.perform(patch("/v1/orders")
                        .cookie(authCookie()))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.error").value("Method Not Allowed"));
    }

    @Test
    void shouldCreateOrder() throws Exception {
        Category category = saveRootCategory("Drivers");
        Product firstProduct = saveProduct("Driver 24W", category);
        Product secondProduct = saveProduct("Driver 48W", category);
        Company company = saveCompany("ACME", "ACME Supplies", CompanyType.SUPPLIER);

        mockMvc.perform(post("/v1/orders")
                        .cookie(authCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "purchaseDate": "2026-04-10",
                                  "lot": "L-2026-003",
                                  "productIds": [%d, %d],
                                  "companyId": %d
                                }
                                """.formatted(firstProduct.getId(), secondProduct.getId(), company.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.purchaseDate").value("2026-04-10"))
                .andExpect(jsonPath("$.lot").value("L-2026-003"))
                .andExpect(jsonPath("$.company.shortName").value("ACME"))
                .andExpect(jsonPath("$.products.length()").value(2))
                .andExpect(jsonPath("$.createdAt").exists());

        assertThat(orderRepository.findAll())
                .anySatisfy(order -> {
                    assertThat(order.getLot()).isEqualTo("L-2026-003");
                    assertThat(order.getPurchaseDate()).isEqualTo(LocalDate.of(2026, 4, 10));
                    assertThat(order.getCompany().getId()).isEqualTo(company.getId());
                    assertThat(order.getProducts()).extracting(Product::getId).containsExactlyInAnyOrder(firstProduct.getId(), secondProduct.getId());
                });
    }

    @Test
    void shouldUpdateOrder() throws Exception {
        Category category = saveRootCategory("Drivers");
        Product originalProduct = saveProduct("Driver 24W", category);
        Product updatedProduct = saveProduct("Driver 60W", category);
        Company originalCompany = saveCompany("ACME", "ACME Supplies", CompanyType.SUPPLIER);
        Company updatedCompany = saveCompany("DLED", "DLED Lighting", CompanyType.OWN);
        Order order = saveOrder(LocalDate.of(2026, 4, 9), "L-2026-004", originalCompany, originalProduct);

        mockMvc.perform(put("/v1/orders/{id}", order.getId())
                        .cookie(authCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "purchaseDate": "2026-04-11",
                                  "lot": "L-2026-005",
                                  "productIds": [%d],
                                  "companyId": %d
                                }
                                """.formatted(updatedProduct.getId(), updatedCompany.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.purchaseDate").value("2026-04-11"))
                .andExpect(jsonPath("$.lot").value("L-2026-005"))
                .andExpect(jsonPath("$.company.shortName").value("DLED"))
                .andExpect(jsonPath("$.products.length()").value(1))
                .andExpect(jsonPath("$.products[0].name").value("Driver 60W"));

        Order updated = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(updated.getLot()).isEqualTo("L-2026-005");
        assertThat(updated.getPurchaseDate()).isEqualTo(LocalDate.of(2026, 4, 11));
        assertThat(updated.getCompany().getId()).isEqualTo(updatedCompany.getId());
        assertThat(updated.getProducts()).extracting(Product::getId).containsExactly(updatedProduct.getId());
    }

    @Test
    void shouldDeleteOrder() throws Exception {
        Category category = saveRootCategory("Drivers");
        Product product = saveProduct("Driver 24W", category);
        Company company = saveCompany("ACME", "ACME Supplies", CompanyType.SUPPLIER);
        Order order = saveOrder(LocalDate.of(2026, 4, 10), "L-2026-006", company, product);

        mockMvc.perform(delete("/v1/orders/{id}", order.getId())
                        .cookie(authCookie()))
                .andExpect(status().isNoContent());

        assertThat(orderRepository.findById(order.getId())).isEmpty();
    }
}
