package br.com.dled.dledbackend.modules.users.application.exception;

import br.com.dled.dledbackend.core.exceptions.DledbackendException;

public class UsernameAlreadyExistsException extends DledbackendException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
