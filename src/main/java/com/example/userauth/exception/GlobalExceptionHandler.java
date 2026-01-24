package com.example.userauth.exception;

import com.example.userauth.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

 @ExceptionHandler(ApiException.class)
public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {

    String msg = ex.getMessage().toLowerCase();
    HttpStatus status = HttpStatus.BAD_REQUEST;

    if (msg.contains("already registered")) {
        status = HttpStatus.CONFLICT; // 409
    } else if (msg.contains("otp") && msg.contains("wait")) {
        status = HttpStatus.TOO_MANY_REQUESTS; // 429
    } else if (msg.contains("not verified")) {
        status = HttpStatus.UNPROCESSABLE_ENTITY; // 422
    }

    ApiResponse<Void> response =
        new ApiResponse<>(false, ex.getMessage(), null);

    return ResponseEntity.status(status).body(response);
}


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleOther(Exception ex) {
        ApiResponse<Void> resp = new ApiResponse<>(false, "Internal server error: " + ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
}
