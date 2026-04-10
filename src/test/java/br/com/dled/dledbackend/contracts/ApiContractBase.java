package br.com.dled.dledbackend.contracts;

import br.com.dled.dledbackend.modules.categories.domain.Category;
import br.com.dled.dledbackend.modules.products.domain.Product;
import br.com.dled.dledbackend.support.AbstractWebIntegrationTest;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;

public class ApiContractBase extends AbstractWebIntegrationTest {

    @BeforeEach
    void setUpContractData() throws Exception {
        Category category = saveRootCategory("Drivers");
        Product product = saveProduct("Driver 24W", category);

        if (category.getId() == null || product.getId() == null) {
            throw new IllegalStateException("Contract data was not created");
        }

        var authCookie = authCookie();
        RestAssuredMockMvc.mockMvc(mockMvc);
        RestAssuredMockMvc.requestSpecification = RestAssuredMockMvc.given()
                .cookie(authCookie.getName(), authCookie.getValue());
    }
}
