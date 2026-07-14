package br.com.dled.dledbackend.modules.wood.application.exception;

import br.com.dled.dledbackend.core.exceptions.DledbackendException;

public class WoodVariationNotFoundException extends DledbackendException {
    public WoodVariationNotFoundException(String message) {
        super(message);
    }
}
