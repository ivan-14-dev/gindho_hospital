package com.gindho.patient.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("Email déjà utilisé: " + email);
    }
}
