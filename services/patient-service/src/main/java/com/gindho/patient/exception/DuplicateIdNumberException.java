package com.gindho.patient.exception;

public class DuplicateIdNumberException extends RuntimeException {
    public DuplicateIdNumberException(String idNumber) {
        super("Numéro d'identification déjà utilisé: " + idNumber);
    }
}
