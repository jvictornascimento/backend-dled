package br.com.dled.dledbackend.modules.companies.application.exception;

public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException(String message) {
        super(message);
    }
}
