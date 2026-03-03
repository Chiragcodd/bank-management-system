package com.example.bank_management_system.exception;

import com.example.bank_management_system.dto.ApiResponse;
import java.util.Optional;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<String>> handleCustom(CustomException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ApiResponse<String>> handleValidation(
        MethodArgumentNotValidException ex) {

    String errorMsg = Optional.ofNullable(ex.getBindingResult().getFieldError())
            .map(error -> error.getDefaultMessage())
            .orElse("Invalid request data");

    return ResponseEntity.badRequest()
            .body(new ApiResponse<>(false, errorMsg, null));
}

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }
}

