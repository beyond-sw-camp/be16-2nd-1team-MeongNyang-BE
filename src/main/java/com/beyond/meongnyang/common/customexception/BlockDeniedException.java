package com.beyond.meongnyang.common.customexception;

public class BlockDeniedException extends RuntimeException{
    public BlockDeniedException(String errorMessage) {
        super(errorMessage);
    }
}
