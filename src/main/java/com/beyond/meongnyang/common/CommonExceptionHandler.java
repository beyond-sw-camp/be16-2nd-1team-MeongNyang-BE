package com.beyond.meongnyang.common;

import com.beyond.meongnyang.common.dto.CommonRes;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonRes.ofFailure(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonRes.ofFailure(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<?> handleEntityExistsException(EntityExistsException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonRes.ofFailure(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonRes.ofFailure(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage());
        String message = e.getConstraintViolations().iterator().next().getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonRes.ofFailure(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        String message = e.getBindingResult().getFieldErrors().iterator().next().getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonRes.ofFailure(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessDeniedException(AccessDeniedException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CommonRes.ofFailure(HttpStatus.FORBIDDEN.value(), e.getMessage()));
    }

//    거래글 수정시 이미지 삭제하기 위한 핸들러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CommonRes.ofFailure(HttpStatus.FORBIDDEN.value(), e.getMessage()));
    }
}
