package com.example.springbootstarterkit.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
    log.warn("Business error: path={}, code={}, msg={}", request.getRequestURI(), ex.getCode(), ex.getMessage());
    ErrorResponse body = new ErrorResponse(ex.getCode(), ex.getMessage());
    return ResponseEntity.status(ex.getStatus()).body(body);
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
  public ResponseEntity<ErrorResponse> handleValidation(Exception ex, HttpServletRequest request) {
    log.warn("Validation error: path={}, msg={}", request.getRequestURI(), ex.getMessage());
    ErrorResponse body = new ErrorResponse("BAD_REQUEST", "요청 값이 올바르지 않습니다.");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnknown(Exception ex, HttpServletRequest request) {
    log.error("Unknown error: path={}, msg={}", request.getRequestURI(), ex.getMessage(), ex);
    ErrorResponse body = new ErrorResponse("INTERNAL_SERVER_ERROR", "서버 에러가 발생했습니다.");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}

