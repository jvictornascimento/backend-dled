package br.com.dled.dledbackend.modules.products.application.exception;

import br.com.dled.dledbackend.core.exceptions.DledbackendException;

public class ProductGalleryLimitException extends DledbackendException {
    public ProductGalleryLimitException(String message) {
        super(message);
    }
}
