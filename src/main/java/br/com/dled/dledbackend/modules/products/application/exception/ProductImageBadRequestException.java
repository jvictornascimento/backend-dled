package br.com.dled.dledbackend.modules.products.application.exception;

import br.com.dled.dledbackend.core.exceptions.DledbackendException;

public class ProductImageBadRequestException extends DledbackendException {
    public ProductImageBadRequestException(String message) {
        super(message);
    }
}
