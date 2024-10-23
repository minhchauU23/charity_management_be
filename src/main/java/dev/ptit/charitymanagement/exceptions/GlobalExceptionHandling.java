package dev.ptit.charitymanagement.exceptions;

import dev.ptit.charitymanagement.dtos.APIResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandling  {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<APIResponse> appExceptionHandling(AppException exception, HttpServletRequest request){
        ErrorCode errorCode = exception.getErrorCode();
        APIResponse apiResponse = APIResponse.builder()
                .time(new Date())
                .code(errorCode.getCode())
                .message("error")
                .errors(Map.of(errorCode.name(),errorCode.getMessage()))
                .method(request.getMethod())
                .endpoint(request.getRequestURI())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }
//    @ExceptionHandler({AuthenticationException.class})
//    public ResponseEntity<APIResponse> authenticationExceptionHandling(AuthenticationException exception, HttpServletRequest request){
//        System.out.println("in auth exception");
//        APIResponse apiResponse = APIResponse.builder()
//                .time(new Date())
//                .errors(Map.of(,exception.getMessage()) )
//                .method(request.getMethod().toString())
//                .endpoint(request.getRequestURI())
//                .build();
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
//    }


//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<APIResponse> accessDeniedExceptionHandling(AccessDeniedException exception, HttpServletRequest request){
//        System.out.println("in denied exception");
//        APIResponse apiResponse = APIResponse.builder()
//                .time(new Date())
//                .error(exception.getMessage())
//                .method(request.getMethod().toString())
//                .endpoint(request.getRequestURI())
//                .build();
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
//    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<APIResponse> badCredentialsExceptionHandling(BadCredentialsException exception, HttpServletRequest request){
        ErrorCode errorCode = ErrorCode.BAD_CREDENTIALS;
        System.out.println("in global");
        APIResponse apiResponse = APIResponse.builder()
                .time(new Date())
                .code(errorCode.getCode())
                .message("error")
                .errors(Map.of(errorCode.name(), errorCode.getMessage()))
                .method(request.getMethod())
                .endpoint(request.getRequestURI())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public  ResponseEntity<APIResponse> methodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request){
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String codeKey = error.getDefaultMessage();
            errors.put(codeKey, ErrorCode.valueOf(codeKey).getMessage());
        });
        APIResponse apiResponse = APIResponse.builder()
                .time(new Date())
                .code(errorCode.getCode())
                .message("error")
                .errors(errors)
                .method(request.getMethod())
                .endpoint(request.getRequestURI())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<APIResponse> noHandlerExceptionHandling(NoHandlerFoundException exception, HttpServletRequest request){
        ErrorCode errorCode = ErrorCode.END_POINT_NOT_FOUND;
        APIResponse apiResponse = APIResponse.builder()
                .time(new Date())
                .code(errorCode.getCode())
                .message("error")
                .errors(Map.of(errorCode.name(),exception.getMessage()))
                .method(request.getMethod())
                .endpoint(request.getRequestURI())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

}
