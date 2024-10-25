package dev.ptit.charitymanagement.service.reset_password;

import dev.ptit.charitymanagement.dtos.request.user.ForgotPasswordRequest;
import dev.ptit.charitymanagement.dtos.request.user.ResetPasswordRequest;
import dev.ptit.charitymanagement.entity.EmailNotification;
import dev.ptit.charitymanagement.entity.Notification;
import dev.ptit.charitymanagement.entity.FirebaseNotification;
import dev.ptit.charitymanagement.entity.User;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.UserRepository;
import dev.ptit.charitymanagement.service.notification.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResetPasswordServiceImpl implements ResetPasswordService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    CacheManager cacheManager;
    NotificationService notificationService;
    @Override
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

    @Override
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

//    @Cacheable(value = "reset_password", key = "#email")
    public String getResetCode(String email){
        Object savedResetCode = cacheManager.getCache("reset_password").get(email).get();
        if(savedResetCode == null){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return savedResetCode.toString();
    }

//    @CachePut( value = "reset_password", key = "#email")
    private String createResetCode(String email){
        String resetCode = UUID.randomUUID().toString();
        cacheManager.getCache("reset_password").put(email, resetCode);
        return resetCode;
    }

}
