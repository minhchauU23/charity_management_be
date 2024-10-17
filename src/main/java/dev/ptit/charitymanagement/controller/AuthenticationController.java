package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.request.auth.LoginRequest;
import dev.ptit.charitymanagement.dtos.request.auth.RegisterRequest;
import dev.ptit.charitymanagement.dtos.response.auth.RegisterResponse;
import dev.ptit.charitymanagement.dtos.response.auth.Token;
import dev.ptit.charitymanagement.service.AuthenticationService;
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
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity authenticate(@RequestBody @Valid LoginRequest loginRequest, HttpServletRequest request){
        Token token =  authenticationService.authenticate(loginRequest);
        return ResponseEntity.ok(APIResponse.builder()
                        .time(new Date())
                        .message("ok")
                        .method(request.getMethod().toString())
                        .endpoint(request.getRequestURI().toString())
                        .data(token)
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterRequest registerRequest, HttpServletRequest request){
        RegisterResponse response = authenticationService.signup(registerRequest);
        return  ResponseEntity.ok(APIResponse.builder()
                .time(new Date())
                .message("ok")
                .method(request.getMethod().toString())
                .endpoint(request.getRequestURI().toString())
                .data(response)
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity refresh(){
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/logout")
    public ResponseEntity logOut(){
        return ResponseEntity.ok("ok");
    }

}
