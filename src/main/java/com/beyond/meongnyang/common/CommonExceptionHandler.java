package com.beyond.meongnyang.common;

import com.beyond.meongnyang.common.dto.CommonErrorDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

    //TODO: ERORR 코드 수정 해야함 maybe?
    // 해당 값을 사용중일 때
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> wrongArgs (IllegalArgumentException e) {
        return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    // 입력한 값이 db에 없을 때
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> entityError (EntityNotFoundException e) {
        return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // 입력해야하는 방식으로 입력을 안 했을 때
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validationError (MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), errorMessage),HttpStatus.BAD_REQUEST);
    }

}
