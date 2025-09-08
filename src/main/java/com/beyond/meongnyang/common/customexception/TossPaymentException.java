package com.beyond.meongnyang.common.customexception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class TossPaymentException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public TossPaymentException(HttpStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public TossPaymentException(HttpStatusCode statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}