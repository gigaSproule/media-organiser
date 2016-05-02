package com.benjaminsproule.mediaorganiser.exception;

public class InvalidDateException extends Exception {

    private static final long serialVersionUID = -8692313005159937239L;

    public InvalidDateException(String message) {
        super(message);
    }
}
