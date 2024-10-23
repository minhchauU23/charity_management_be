package dev.ptit.charitymanagement.service.auth;

import dev.ptit.charitymanagement.dtos.request.auth.LoginRequest;
import dev.ptit.charitymanagement.dtos.request.auth.LogoutRequest;
import dev.ptit.charitymanagement.dtos.request.auth.RefreshTokenRequest;
import dev.ptit.charitymanagement.dtos.request.auth.RegisterRequest;
import dev.ptit.charitymanagement.dtos.response.auth.AuthenticationResponse;
import dev.ptit.charitymanagement.dtos.response.auth.RegisterResponse;
import dev.ptit.charitymanagement.dtos.response.auth.Token;
import dev.ptit.charitymanagement.dtos.response.user.UserResponse;
import dev.ptit.charitymanagement.entity.Role;
import dev.ptit.charitymanagement.entity.User;
import dev.ptit.charitymanagement.entity.UserRole;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.UserRepository;
import dev.ptit.charitymanagement.service.user.UserService;
import dev.ptit.charitymanagement.utils.JWTUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    UserService userService;
    UserRepository userRepository;
    AuthenticationManager authenticationManager;
    JWTUtils jwtUtils;
    PasswordEncoder passwordEncoder;
    @Override
    public AuthenticationResponse authenticate(LoginRequest loginRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        User user = (User) auth.getPrincipal();
        Token token = jwtUtils.generateToken(auth);
        UserResponse userInfor = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
        return AuthenticationResponse.builder()
                .token(token)
                .infor(userInfor)
                .build();
    }

    @Override
    public AuthenticationResponse refresh(RefreshTokenRequest request) {
        boolean isValidToken = jwtUtils.validateRefreshToken(request.getRefreshToken());
        if(!isValidToken){
            throw new AppException(ErrorCode.INVALID_KEY);
        }
        User user = userRepository.findUserWithRole(jwtUtils.extractRefresh(request.getRefreshToken()).getSubject()).orElseThrow(() -> new RuntimeException("s"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
        Token token = jwtUtils.generateToken(auth);
        token.setRefreshToken(request.getRefreshToken());
        UserResponse userInfor = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
        return AuthenticationResponse.builder()
                .token(token)
                .infor(userInfor)
                .build();
    }

    @Override
    public RegisterResponse signup(RegisterRequest registerRequest) {
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        Role role = new Role();
        role.setId(2L);

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
//                .dob(registerRequest.getDob())
//                .gender(registerRequest.getGender())
                .email(registerRequest.getEmail())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phone(registerRequest.getPhone())
//                .address(registerRequest.getAddress())
                .build();
        UserRole userRole = new UserRole();
        userRole.setRole(role);
        userRole.setUser(user);
        user.setEnabled(true);
        user.setUserRoles(Set.of(userRole));
        user = userRepository.save(user);
        System.out.println(user.isEnabled());
        return RegisterResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .address(user.getAddress())
                .gender(user.getGender())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    @Override
    public void logout(LogoutRequest request) {

    }
}
