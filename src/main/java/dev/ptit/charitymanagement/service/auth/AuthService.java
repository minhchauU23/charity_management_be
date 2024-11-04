package dev.ptit.charitymanagement.service.auth;

import dev.ptit.charitymanagement.dtos.request.auth.*;
import dev.ptit.charitymanagement.dtos.response.auth.AuthenticationResponse;
import dev.ptit.charitymanagement.dtos.response.auth.Token;
import dev.ptit.charitymanagement.dtos.response.role.RoleDTO;
import dev.ptit.charitymanagement.dtos.response.user.UserDTO;
import dev.ptit.charitymanagement.entity.EmailNotification;
import dev.ptit.charitymanagement.entity.Notification;
import dev.ptit.charitymanagement.entity.User;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.UserRepository;
import dev.ptit.charitymanagement.service.notification.NotificationService;
import dev.ptit.charitymanagement.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
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
        Token token = new Token();
        token.setAccessToken(jwtUtils.generateAccessToken(auth));
        token.setRefreshToken(jwtUtils.generateRefreshToken(auth));
        UserDTO userInfor = UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(auth.getAuthorities().stream().map(grantedAuthority -> RoleDTO.builder()
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

        Claims claims = jwtUtils.extractRefresh(request.getRefreshToken());
        Object latestBlackList =  cacheManager.getCache("jwt_blacklist").get(claims.getSubject()).get();
        if(latestBlackList != null && ((Date )latestBlackList).after(claims.getIssuedAt())){
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        User user = userRepository.findByEmailWithRoles(jwtUtils.extractRefresh(request.getRefreshToken()).getSubject()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
        Token token = new Token();
        token.setAccessToken(jwtUtils.generateAccessToken(auth));
        token.setRefreshToken(request.getRefreshToken());
        UserDTO userInfor = UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(auth.getAuthorities().stream().map(grantedAuthority -> RoleDTO.builder()
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


    public void forgotPassword(ForgotPasswordRequest request) {
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

    }

    public void resetPassword(ResetPasswordRequest request) {
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
        cacheManager.getCache("jwt_blacklist").put(user.getEmail(), new Date());
        Date date = (Date) cacheManager.getCache("jwt_blacklist").get(user.getEmail()).get();
        log.info("change at {}", date);
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
