package dev.ptit.charitymanagement.service.notification.component;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import dev.ptit.charitymanagement.entity.Notification;
import dev.ptit.charitymanagement.entity.PushNotification;
import dev.ptit.charitymanagement.service.notification.NotificationStrategy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PushNotificationStrategy implements NotificationStrategy {
    FirebaseMessaging fcm;
    @Override
    public void send(Notification notification) {
        Message message = Message.builder()
                .setTopic("testTopic")
                .putData("title", notification.getTitle())
                .putData("content",notification.getContent())
                .build();
        try {
            String id = fcm.send(message);
            log.info("Send to topic testTopic success");
            log.info("id message: {}", id);
        } catch (FirebaseMessagingException e) {
//            throw new RuntimeException(e);
            log.error("Error send to topic: {}", "testTopic");
        }
    }

    @Override
    public boolean support(Class<?> notification) {
        return PushNotification.class.isAssignableFrom(notification);
    }
}
