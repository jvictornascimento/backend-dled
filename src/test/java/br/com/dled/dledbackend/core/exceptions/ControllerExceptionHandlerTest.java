package br.com.dled.dledbackend.core.exceptions;

import br.com.dled.dledbackend.infrastructure.security.InvalidApiKeyException;
import br.com.dled.dledbackend.modules.categories.application.exception.CategoryNotFoundException;
import br.com.dled.dledbackend.modules.products.application.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControllerExceptionHandlerTest {

    private final ControllerExceptionHandler handler = new ControllerExceptionHandler();

    @Test
    void shouldHandleGenericException() {
        MockHttpServletRequest request = request("/v1/products");

        StandardError response = handler.internalServerError(new RuntimeException("boom"), request).getBody();

        assertEquals(500, response.status());
        assertEquals("An error occurred during processing", response.error());
        assertEquals("boom", response.message());
    }

    @Test
    void shouldHandleMethodNotAllowed() {
        MockHttpServletRequest request = request("/v1/products");

        StandardError response = handler.methodNotAllowed(
                new HttpRequestMethodNotSupportedException("POST"),
                request
        ).getBody();

        assertEquals(405, response.status());
        assertEquals("The method [GET] is not allowed for this resource", response.error());
    }

    @Test
    void shouldHandleCategoryNotFound() {
        MockHttpServletRequest request = request("/v1/categories/99");

        StandardError response = handler.categoryNotFound(new CategoryNotFoundException("Category not found"), request).getBody();

        assertEquals(404, response.status());
        assertEquals("User not found", response.error());
    }

    @Test
    void shouldHandleProductNotFound() {
        MockHttpServletRequest request = request("/v1/products/99");

        StandardError response = handler.productNotFound(new ProductNotFoundException("Product not found"), request).getBody();

        assertEquals(404, response.status());
        assertEquals("Product not found", response.error());
    }

    @Test
    void shouldHandleInvalidApiKey() {
        MockHttpServletRequest request = request("/v1/products");

        StandardError response = handler.invalidApiKey(new InvalidApiKeyException("Invalid or missing API key"), request).getBody();

        assertEquals(401, response.status());
        assertEquals("Invalid or missing API key", response.error());
    }

    private MockHttpServletRequest request(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI(uri);
        return request;
    }
}
