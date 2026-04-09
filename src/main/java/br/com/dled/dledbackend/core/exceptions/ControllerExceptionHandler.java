package br.com.dled.dledbackend.core.exceptions;

import br.com.dled.dledbackend.modules.categories.application.exception.CategoryNotFoundException;
import br.com.dled.dledbackend.modules.products.application.exception.ProductNotFoundException;
import br.com.dled.dledbackend.infrastructure.security.InvalidApiKeyException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.*;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> internalServerError(Exception e, HttpServletRequest request) {
        var error = GENERIC_EXCEPTION.getMassage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        var standardError = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(standardError);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<StandardError> methodNotAllowed(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        var error = GENERIC_METHOD_NOT_ALLOW.params(request.getMethod()).getMassage();
        HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
        var standardError = new StandardError( Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(standardError);
    }
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<StandardError> categoryNotFound(CategoryNotFoundException e, HttpServletRequest request) {
        var error = CATEGORY_NOT_FOUND.getMassage();
        HttpStatus status = HttpStatus.NOT_FOUND;
        var standardError = new StandardError( Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(standardError);
    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<StandardError> productNotFound(ProductNotFoundException e, HttpServletRequest request) {
        var error = PRODUCT_NOT_FOUND.getMassage();
        HttpStatus status = HttpStatus.NOT_FOUND;
        var standardError = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(standardError);
    }
    @ExceptionHandler(InvalidApiKeyException.class)
    public ResponseEntity<StandardError> invalidApiKey(InvalidApiKeyException e, HttpServletRequest request) {
        var error = INVALID_API_KEY.getMassage();
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        var standardError = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(standardError);
    }
}
