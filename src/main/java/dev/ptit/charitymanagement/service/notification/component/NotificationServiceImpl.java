package dev.ptit.charitymanagement.service.notification.component;

import dev.ptit.charitymanagement.dtos.request.notification.NotificationTokenRequest;
import dev.ptit.charitymanagement.entity.Notification;
import dev.ptit.charitymanagement.entity.NotificationToken;
import dev.ptit.charitymanagement.entity.User;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.NotificationTokenRepository;
import dev.ptit.charitymanagement.repository.UserRepository;
import dev.ptit.charitymanagement.service.notification.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {
    UserRepository userRepository;
    NotificationTokenRepository notificationTokenRepository;
    RabbitTemplate template;
    List<String> queuesName = List.of("EMAIL", "FIREBASE");
    @Override
    public void send(Notification notification) {
        for(String queue: queuesName){
            if(notification.getType().equals(queue)){
                template.convertAndSend(queue, notification);
                log.info("Sent to {}", queue);
            }
        }
    }

    @Override
    public void sendAll(Notification notification) {

    }

    public void newNotificationTokenUser(Long userId,NotificationTokenRequest request){
        if(!notificationTokenRepository.existsByRegistrationAndUserId(request.getRegistration(),userId)){
            createNotificationToken(userId,request);
        }
        else{
            activeToken(request.getRegistration(), userId);
        }

    }

    @Override
    public void createNotificationToken(Long userId,NotificationTokenRequest request) {
//        User user = new User();
//        user.setId(request.getUserId());
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        NotificationToken token = NotificationToken.builder()
                .registration(request.getRegistration())
                .user(user)
                .isActive(true)
                .build();
        notificationTokenRepository.save(token);
    }

    public void inActiveToken(String registration, Long userId){
        NotificationToken token = notificationTokenRepository.findByRegistrationAndUserId(registration, userId).orElseThrow(() -> new RuntimeException("err"));
        token.setActive(false);
        notificationTokenRepository.save(token);
    }

    public void activeToken(String registration, Long userId){
        NotificationToken token = notificationTokenRepository.findByRegistrationAndUserId(registration, userId).orElseThrow(() -> new RuntimeException("err"));
        token.setActive(true);
        notificationTokenRepository.save(token);
    }

}
