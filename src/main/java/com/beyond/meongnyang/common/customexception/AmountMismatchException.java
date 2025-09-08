package com.beyond.meongnyang.common.customexception;

public class AmountMismatchException extends RuntimeException {
    public AmountMismatchException(String message) {
        super(message);
    }
}
