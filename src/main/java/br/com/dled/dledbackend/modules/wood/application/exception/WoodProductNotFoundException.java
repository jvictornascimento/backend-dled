package br.com.dled.dledbackend.modules.wood.application.exception;

import br.com.dled.dledbackend.core.exceptions.DledbackendException;

public class WoodProductNotFoundException extends DledbackendException {
    public WoodProductNotFoundException(String message) {
        super(message);
    }
}
