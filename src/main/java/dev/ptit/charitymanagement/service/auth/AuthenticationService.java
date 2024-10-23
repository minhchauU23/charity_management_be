package dev.ptit.charitymanagement.service.auth;

import dev.ptit.charitymanagement.dtos.request.auth.LoginRequest;
import dev.ptit.charitymanagement.dtos.request.auth.LogoutRequest;
import dev.ptit.charitymanagement.dtos.request.auth.RefreshTokenRequest;
import dev.ptit.charitymanagement.dtos.request.auth.RegisterRequest;
import dev.ptit.charitymanagement.dtos.response.auth.AuthenticationResponse;
import dev.ptit.charitymanagement.dtos.response.auth.RegisterResponse;

public interface AuthenticationService {
    AuthenticationResponse authenticate(LoginRequest authenticationRequest);
    AuthenticationResponse refresh(RefreshTokenRequest request);
    RegisterResponse signup(RegisterRequest registerRequest);
    void logout(LogoutRequest request);

}
