package br.com.dled.dledbackend.modules.wood.application.exception;

import br.com.dled.dledbackend.core.exceptions.DledbackendException;

public class WoodCategoryNotFoundException extends DledbackendException {
    public WoodCategoryNotFoundException(String message) {
        super(message);
    }
}
