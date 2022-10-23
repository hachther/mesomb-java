package com.hachther.mesomb.exceptions;

public class ServerException extends Exception {
    private final String code;

    public ServerException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
