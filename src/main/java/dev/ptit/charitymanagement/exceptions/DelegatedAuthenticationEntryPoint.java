package dev.ptit.charitymanagement.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ptit.charitymanagement.dtos.APIResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Component("delegatedAuthenticationEntryPoint")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DelegatedAuthenticationEntryPoint implements AuthenticationEntryPoint {
    ObjectMapper objectMapper;



    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
        APIResponse apiResponse =  APIResponse.builder()
                .time(new Date())
                .code(errorCode.getCode())
                .message("error")
                .method(request.getMethod())
                .endpoint(request.getRequestURI())
                .errors(Map.of(errorCode.name(), errorCode.getMessage()))
                .build();


        String obj = objectMapper.writeValueAsString(apiResponse);
        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(obj);
    }
}