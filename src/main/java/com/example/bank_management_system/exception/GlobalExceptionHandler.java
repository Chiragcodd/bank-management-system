package com.example.bank_management_system.exception;

import com.example.bank_management_system.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ✅ ADD: Logger — errors track karne ke liye
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<String>> handleCustom(CustomException ex) {
        // ✅ ADD: Log karo — debug mein kaam aayega
        log.warn("Business error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidation(
            MethodArgumentNotValidException ex) {

        String errorMsg = Optional.ofNullable(ex.getBindingResult().getFieldError())
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request data");

        log.warn("Validation error: {}", errorMsg);
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, errorMsg, null));
    }

    // ✅ ADD: Access denied — koi USER admin endpoint access kare
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(false, "Access denied: You don't have permission", null));
    }

    // ✅ FIX: Generic exception mein error message seedha mat bhejo — internal details leak hoti hain
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
        log.error("Unexpected error: ", ex);  // Full stack trace log mein
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Something went wrong, please try again", null));
    }
}