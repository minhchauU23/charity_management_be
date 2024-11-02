package dev.ptit.charitymanagement.service.auth;

import dev.ptit.charitymanagement.dtos.request.auth.LoginRequest;
import dev.ptit.charitymanagement.dtos.request.auth.LogoutRequest;
import dev.ptit.charitymanagement.dtos.request.auth.RefreshTokenRequest;
import dev.ptit.charitymanagement.dtos.request.auth.RegisterRequest;
import dev.ptit.charitymanagement.dtos.request.user.ForgotPasswordRequest;
import dev.ptit.charitymanagement.dtos.request.user.ResetPasswordRequest;
import dev.ptit.charitymanagement.dtos.response.auth.AuthenticationResponse;
import dev.ptit.charitymanagement.dtos.response.auth.RegisterResponse;
import dev.ptit.charitymanagement.dtos.response.auth.Token;
import dev.ptit.charitymanagement.dtos.response.role.RoleResponse;
import dev.ptit.charitymanagement.dtos.response.user.UserResponse;
import dev.ptit.charitymanagement.entity.*;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.UserRepository;
import dev.ptit.charitymanagement.service.notification.NotificationService;
import dev.ptit.charitymanagement.utils.JWTUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthService  {
    UserRepository userRepository;
    JWTUtils jwtUtils;
    CacheManager cacheManager;
    AuthenticationManager authenticationManager;
    NotificationService notificationService;
    PasswordEncoder passwordEncoder;
    public AuthenticationResponse login(LoginRequest loginRequest) {
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
                .roles(auth.getAuthorities().stream().map(grantedAuthority -> RoleResponse.builder()
                        .name(grantedAuthority.getAuthority())
                        .build()).toList())
                .build();
        return AuthenticationResponse.builder()
                .token(token)
                .infor(userInfor)
                .build();
    }

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
                .roles(auth.getAuthorities().stream().map(grantedAuthority -> RoleResponse.builder()
                        .name(grantedAuthority.getAuthority())
                        .build()).toList())
                .build();
        return AuthenticationResponse.builder()
                .token(token)
                .infor(userInfor)
                .build();
    }

    public void logout(LogoutRequest request) {

    }

//    public RegisterResponse signup(RegisterRequest registerRequest) {
//        if(userRepository.existsByEmail(registerRequest.getEmail())){
//            throw new AppException(ErrorCode.USER_EXISTED);
//        }
//        Role role = new Role();
//        role.setId(2L);
//
//        User user = User.builder()
//                .email(registerRequest.getEmail())
//                .password(passwordEncoder.encode(registerRequest.getPassword()))
//                .email(registerRequest.getEmail())
//                .firstName(registerRequest.getFirstName())
//                .lastName(registerRequest.getLastName())
//                .phone(registerRequest.getPhone())
//                .build();
//        UserRole userRole = new UserRole();
//        userRole.setRole(role);
//        userRole.setUser(user);
//        user.setEnabled(true);
//        user.setUserRoles(Set.of(userRole));
//        user = userRepository.save(user);
//        System.out.println(user.isEnabled());
//        return RegisterResponse.builder()
//                .id(user.getId())
//                .email(user.getEmail())
//                .address(user.getAddress())
//                .gender(user.getGender())
//                .firstName(user.getFirstName())
//                .lastName(user.getLastName())
//                .build();
//    }




    public boolean forgotPassword(ForgotPasswordRequest request) {
        if(!userRepository.existsByEmail(request.getEmail())){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        String resetPasswordCode = createResetCode(request.getEmail());
        Notification notification = new EmailNotification();
        notification.setType("EMAIL");
        notification.setTitle("No reply");
        notification.setReceipt(request.getEmail());
        notification.setContent("This is your reset password code: " + resetPasswordCode);
        notificationService.send(notification);

//        Notification notificationPush = new FirebaseNotification();
//        notificationPush.setType("PUSH");
//        notificationPush.setTitle("No reply");
//        notificationPush.setReceipt(request.getEmail());
//        notificationPush.setContent("This is your reset password code: " + resetPasswordCode);
//        notificationService.send(notificationPush);
        return true;
    }

    public boolean resetPassword(ResetPasswordRequest request) {
        String savedResetCode = getResetCode(request.getEmail());
        System.out.println(savedResetCode);
        if(!request.getResetPasswordCode().equals(savedResetCode)){
            throw new AppException(ErrorCode.RESET_PASSWORD_CODE_INVALID);
        }
        if(!request.getPassword().equals(request.getRepeatPassword())){
            throw new AppException(ErrorCode.REPEAT_PASSWORD_NOT_MATCHING);
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return true;
    }

    private String getResetCode(String email){
        Object savedResetCode = cacheManager.getCache("reset_password").get(email).get();
        if(savedResetCode == null){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return savedResetCode.toString();
    }

    private String createResetCode(String email){
        String resetCode = UUID.randomUUID().toString();
        cacheManager.getCache("reset_password").put(email, resetCode);
        return resetCode;
    }
}