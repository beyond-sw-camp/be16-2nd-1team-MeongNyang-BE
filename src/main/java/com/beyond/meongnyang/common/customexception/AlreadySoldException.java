package com.beyond.meongnyang.common.customexception;

public class AlreadySoldException extends RuntimeException {
    public AlreadySoldException() {
        super();
    }

    public AlreadySoldException(String message) {
        super(message);
    }

    public AlreadySoldException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadySoldException(Throwable cause) {
        super(cause);
    }
}