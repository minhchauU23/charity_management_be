package dev.ptit.charitymanagement.service;

import dev.ptit.charitymanagement.dtos.request.auth.LoginRequest;
import dev.ptit.charitymanagement.dtos.request.auth.LogoutRequest;
import dev.ptit.charitymanagement.dtos.request.auth.RegisterRequest;
import dev.ptit.charitymanagement.dtos.response.auth.RegisterResponse;
import dev.ptit.charitymanagement.dtos.response.auth.Token;

import java.net.http.HttpRequest;

public interface AuthenticationService {
    Token authenticate(LoginRequest authenticationRequest);
    Token refresh(HttpRequest httpRequest);
    RegisterResponse signup(RegisterRequest registerRequest);
    void logout(LogoutRequest request);

}
