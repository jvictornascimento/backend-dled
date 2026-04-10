package br.com.dled.dledbackend.modules.users.application.exception;

import br.com.dled.dledbackend.core.exceptions.DledbackendException;

public class UserNotFoundException extends DledbackendException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
