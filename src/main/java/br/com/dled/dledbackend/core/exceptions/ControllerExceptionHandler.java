package br.com.dled.dledbackend.core.exceptions;

import br.com.dled.dledbackend.modules.categories.application.exception.CategoryNotFoundException;
import br.com.dled.dledbackend.modules.companies.application.exception.CompanyNotFoundException;
import br.com.dled.dledbackend.modules.orders.application.exception.OrderNotFoundException;
import br.com.dled.dledbackend.modules.products.application.exception.ProductGalleryLimitException;
import br.com.dled.dledbackend.modules.products.application.exception.ProductImageBadRequestException;
import br.com.dled.dledbackend.modules.products.application.exception.ProductNotFoundException;
import br.com.dled.dledbackend.modules.printtemplates.application.exception.PrintTemplateNotFoundException;
import br.com.dled.dledbackend.modules.users.application.exception.UserEmailAlreadyExistsException;
import br.com.dled.dledbackend.modules.users.application.exception.UserNotFoundException;
import br.com.dled.dledbackend.modules.users.application.exception.UsernameAlreadyExistsException;
import br.com.dled.dledbackend.infrastructure.security.InvalidApiKeyException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.GENERIC_EXCEPTION;
import static br.com.dled.dledbackend.core.exceptions.BaseMessageError.GENERIC_METHOD_NOT_ALLOW;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> internalServerError(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return buildResponse(status, GENERIC_EXCEPTION.getMassage(), request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<StandardError> methodNotAllowed(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
        return buildResponse(status, GENERIC_METHOD_NOT_ALLOW.params(request.getMethod()).getMassage(), request);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<StandardError> categoryNotFound(CategoryNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return buildResponse(status, e.getMessage(), request);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<StandardError> productNotFound(ProductNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return buildResponse(status, e.getMessage(), request);
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<StandardError> companyNotFound(CompanyNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return buildResponse(status, e.getMessage(), request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<StandardError> userNotFound(UserNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return buildResponse(status, e.getMessage(), request);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<StandardError> orderNotFound(OrderNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return buildResponse(status, e.getMessage(), request);
    }

    @ExceptionHandler(PrintTemplateNotFoundException.class)
    public ResponseEntity<StandardError> printTemplateNotFound(PrintTemplateNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return buildResponse(status, e.getMessage(), request);
    }

    @ExceptionHandler(InvalidApiKeyException.class)
    public ResponseEntity<StandardError> invalidApiKey(InvalidApiKeyException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return buildResponse(status, e.getMessage(), request);
    }

    @ExceptionHandler({ProductGalleryLimitException.class, ProductImageBadRequestException.class})
    public ResponseEntity<StandardError> badRequest(RuntimeException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return buildResponse(status, e.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> validationError(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation error");
        return buildResponse(status, message, request);
    }

    @ExceptionHandler({UsernameAlreadyExistsException.class, UserEmailAlreadyExistsException.class})
    public ResponseEntity<StandardError> conflict(RuntimeException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        return buildResponse(status, e.getMessage(), request);
    }

    private ResponseEntity<StandardError> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        var standardError = new StandardError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(standardError);
    }
}
