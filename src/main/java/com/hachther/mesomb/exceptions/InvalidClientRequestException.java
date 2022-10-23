package com.hachther.mesomb.exceptions;

public class InvalidClientRequestException extends Exception {
    private final String code;

    public InvalidClientRequestException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
