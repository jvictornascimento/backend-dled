package br.com.dled.dledbackend.contracts;

import br.com.dled.dledbackend.modules.categories.domain.Category;
import br.com.dled.dledbackend.modules.products.domain.Product;
import br.com.dled.dledbackend.support.AbstractWebIntegrationTest;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;

public class ApiContractBase extends AbstractWebIntegrationTest {

    @BeforeEach
    void setUpContractData() {
        Category category = saveRootCategory("Drivers");
        Product product = saveProduct("Driver 24W", category);

        if (category.getId() == null || product.getId() == null) {
            throw new IllegalStateException("Contract data was not created");
        }

        RestAssuredMockMvc.mockMvc(mockMvc);
        RestAssuredMockMvc.requestSpecification = RestAssuredMockMvc.given()
                .header("X-API-Key", apiKey());
    }
}
