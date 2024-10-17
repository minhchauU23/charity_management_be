package dev.ptit.charitymanagement.exceptions;

import dev.ptit.charitymanagement.dtos.APIResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandling {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<APIResponse> appExceptionHandling(AppException exception, HttpServletRequest request){
        APIResponse apiResponse = APIResponse.builder()
                .time(new Date())
                .error(exception.getErrorCode().getMessage())
                .method(request.getMethod().toString())
                .endpoint(request.getRequestURI())
                .build();
        return ResponseEntity.status(exception.getErrorCode().getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<APIResponse> authenticationExceptionHandling(AuthenticationException exception, HttpServletRequest request){
        APIResponse apiResponse = APIResponse.builder()
                .time(new Date())
                .error(exception.getMessage())
                .method(request.getMethod().toString())
                .endpoint(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<APIResponse> badCredentialsExceptionHandling(BadCredentialsException exception, HttpServletRequest request){
        APIResponse apiResponse = APIResponse.builder()
                .time(new Date())
                .error(exception.getMessage())
                .method(request.getMethod())
                .endpoint(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public  ResponseEntity<APIResponse> methodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request){
        Map<String, String> errors = new HashMap<>();

        // Lấy danh sách các lỗi validation và lưu chúng dưới dạng cặp key-value
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        APIResponse apiResponse = APIResponse.builder()
                .time(new Date())
                .error(errors.entrySet().stream().map((entry) -> entry.getKey() + ": " + entry.getValue() + "\n").toList().stream().collect(Collectors.joining()))
                .method(request.getMethod())
                .endpoint(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }


}
