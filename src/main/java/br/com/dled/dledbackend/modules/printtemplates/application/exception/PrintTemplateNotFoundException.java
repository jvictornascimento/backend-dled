package br.com.dled.dledbackend.modules.printtemplates.application.exception;

public class PrintTemplateNotFoundException extends RuntimeException {
    public PrintTemplateNotFoundException(String message) {
        super(message);
    }
}
