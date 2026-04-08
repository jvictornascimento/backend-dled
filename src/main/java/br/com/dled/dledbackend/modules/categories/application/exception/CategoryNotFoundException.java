package br.com.dled.dledbackend.modules.categories.application.exception;

import br.com.dled.dledbackend.core.exceptions.DledbackendException;

public class CategoryNotFoundException extends DledbackendException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
