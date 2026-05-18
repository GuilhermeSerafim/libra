package br.senac.biblioteca.exception;

import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends ApiException {
    public DuplicateEmailException() {
        super(HttpStatus.CONFLICT, "Email ja cadastrado.");
    }
}
