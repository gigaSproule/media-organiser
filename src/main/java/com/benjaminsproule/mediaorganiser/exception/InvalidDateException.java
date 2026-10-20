package com.benjaminsproule.mediaorganiser.exception;

import org.jspecify.annotations.NonNull;

public class InvalidDateException extends Exception {

    public InvalidDateException(@NonNull String message) {
        super(message);
    }
}
