package br.com.dled.dledbackend.infrastructure.security;

import br.com.dled.dledbackend.core.exceptions.DledbackendException;

public class InvalidApiKeyException extends DledbackendException {
    public InvalidApiKeyException(String message) {
        super(message);
    }
}
