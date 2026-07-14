package br.com.dled.dledbackend.modules.users.application.exception;

import br.com.dled.dledbackend.core.exceptions.DledbackendException;

public class UserEmailAlreadyExistsException extends DledbackendException {
    public UserEmailAlreadyExistsException(String message) {
        super(message);
    }
}
