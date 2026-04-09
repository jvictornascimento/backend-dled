package br.com.dled.dledbackend.modules.products.application.exception;

import br.com.dled.dledbackend.core.exceptions.DledbackendException;

public class ProductNotFoundException extends DledbackendException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
