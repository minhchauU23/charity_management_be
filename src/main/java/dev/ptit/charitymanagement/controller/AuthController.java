package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.request.auth.*;
import dev.ptit.charitymanagement.dtos.response.auth.AuthenticationResponse;
import dev.ptit.charitymanagement.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequest loginRequest, HttpServletRequest request){
        AuthenticationResponse response =  authService.login(loginRequest);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .time(new Date())
                .message("ok")
                .method(request.getMethod())
                .endpoint(request.getRequestURI())
                .data(response)
                .build());
    }


    @PostMapping("/forgot-password")
    public  ResponseEntity forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest, HttpServletRequest request){
        authService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest, HttpServletRequest request){
        authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .build());
    }
    @PostMapping("/refresh")
    public ResponseEntity refresh(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest, HttpServletRequest request){

        AuthenticationResponse response =  authService.refresh(refreshTokenRequest);
        return ResponseEntity.ok(APIResponse.builder()
                .time(new Date())
                .code(200)
                .message("ok")
                .method(request.getMethod())
                .endpoint(request.getRequestURI())
                .data(response)
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity logOut(){
        return ResponseEntity.ok("ok");
    }

}
