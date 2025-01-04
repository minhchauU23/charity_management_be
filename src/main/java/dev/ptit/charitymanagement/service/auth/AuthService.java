package dev.ptit.charitymanagement.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ptit.charitymanagement.dtos.*;
import dev.ptit.charitymanagement.dtos.response.auth.Token;

import dev.ptit.charitymanagement.entity.NotificationTemplateEntity;
import dev.ptit.charitymanagement.entity.NotificationType;
import dev.ptit.charitymanagement.entity.UserEntity;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.NotificationTemplateRepository;
import dev.ptit.charitymanagement.repository.UserRepository;
import dev.ptit.charitymanagement.service.notification.impl.NotificationService;
import dev.ptit.charitymanagement.service.notificationTemplate.NotificationTemplateService;
import dev.ptit.charitymanagement.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
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
    NotificationTemplateRepository notificationTemplateRepository;
    NotificationTemplateService notificationTemplateService;
    PasswordEncoder passwordEncoder;
    ObjectMapper objectMapper;
    public AuthenticationResponse login(User user) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
        );
        UserEntity userEntity = (UserEntity) auth.getPrincipal();
        Token token = new Token();
        token.setAccessToken(jwtUtils.generateAccessToken(auth));
        token.setRefreshToken(jwtUtils.generateRefreshToken(auth));

        User userResponse = User.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .roles(auth.getAuthorities().stream().map(grantedAuthority -> Role.builder()
                        .name(grantedAuthority.getAuthority())
                        .build()).toList())
                .build();
        return AuthenticationResponse.builder()
                .token(token)
                .infor(userResponse)
                .build();
    }

    public AuthenticationResponse refresh(RefreshTokenRequest request) {
        log.info("Called at refresh in auth service");
        boolean isValidToken = jwtUtils.validateRefreshToken(request.getRefreshToken());
        if(!isValidToken){
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        Claims claims = jwtUtils.extractRefresh(request.getRefreshToken());
        Cache.ValueWrapper latestBlackList =  cacheManager.getCache("jwt_blacklist").get(claims.getSubject());
        log.info("value wrapper: {}", latestBlackList);
        if(latestBlackList != null && ((Date )latestBlackList.get()).after(claims.getIssuedAt())){
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        UserEntity user = userRepository.findByEmailWithRoles(jwtUtils.extractRefresh(request.getRefreshToken()).getSubject()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
        Token token = new Token();
        token.setAccessToken(jwtUtils.generateAccessToken(auth));
        token.setRefreshToken(request.getRefreshToken());
        User userInfor = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(auth.getAuthorities().stream().map(grantedAuthority -> Role.builder()
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


    public void forgotPassword(ForgotPasswordRequest request)  {
        UserEntity user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String resetPasswordCode = createResetCode(request.getEmail());


        NotificationTemplateEntity template = notificationTemplateRepository.findByName("FORGOT_PASSWORD")
                .orElseThrow(() ->new AppException(ErrorCode.NOTIFICATION_TEMPLATE_NOT_EXISTED));

        String data = "{\"firstName\":\""+user.getFirstName() +"\"," +
                "\"lastName\":\""+user.getLastName() +"\"," +
                "\"code\":\""+ resetPasswordCode + "\"}";
        Notification notification = Notification.builder()
                .type(NotificationType.EMAIL)
                .data(data)
                .destination(User.builder()
                        .email(user.getEmail())
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .build())
                .template(NotificationTemplate.builder()
                        .id(template.getId())
                        .name(template.getName())
                        .build())
                .build();
         notificationService.create(notification);

    }

    public void resetPassword(ResetPasswordRequest request) {
        String savedResetCode = getResetCode(request.getEmail());
        log.info("Saved resetCode {}", savedResetCode);
        log.info("Obtain resetCode {}", request.getResetPasswordCode());
        log.info("equals {}", request.getResetPasswordCode().trim().equals(savedResetCode.trim()));
        if(!request.getResetPasswordCode().equals(savedResetCode)){
            throw new AppException(ErrorCode.RESET_PASSWORD_CODE_INVALID);
        }
        if(!request.getPassword().equals(request.getRepeatPassword())){
            throw new AppException(ErrorCode.REPEAT_PASSWORD_NOT_MATCHING);
        }


        UserEntity user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        cacheManager.getCache("jwt_blacklist").put(user.getEmail(), new Date());
        Date date = (Date) cacheManager.getCache("jwt_blacklist").get(user.getEmail()).get();
        log.info("change at {}", date);
    }

    private String getResetCode(String email){
        Cache.ValueWrapper  savedResetCode = cacheManager.getCache("reset_password").get(email);

        if(savedResetCode == null){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return savedResetCode.get().toString();
    }

    private String createResetCode(String email){
        String resetCode = UUID.randomUUID().toString();
        cacheManager.getCache("reset_password").put(email, resetCode);
        return resetCode;
    }
}
