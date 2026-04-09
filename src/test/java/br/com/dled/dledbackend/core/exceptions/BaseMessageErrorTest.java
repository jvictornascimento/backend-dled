package br.com.dled.dledbackend.core.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseMessageErrorTest {

    @Test
    void shouldReturnMessageWithoutParams() {
        assertEquals("User not found", BaseMessageError.CATEGORY_NOT_FOUND.getMassage());
    }

    @Test
    void shouldFormatMessageWithParams() {
        String message = BaseMessageError.GENERIC_METHOD_NOT_ALLOW.params("POST").getMassage();

        assertEquals("The method [POST] is not allowed for this resource", message);
    }
}
