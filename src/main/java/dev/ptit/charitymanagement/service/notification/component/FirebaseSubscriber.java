package dev.ptit.charitymanagement.service.notification.component;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import dev.ptit.charitymanagement.service.notification.NotificationSubscriber;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FirebaseSubscriber implements NotificationSubscriber {
//    private static final Logger log = LoggerFactory.getLogger(FirebaseSubscriber.class);
    FirebaseMessaging firebaseMessaging;
    @Override
    public String subscribe(List<String> registration, String topic) {
        try {
            TopicManagementResponse response = firebaseMessaging.subscribeToTopic(registration, topic);

        } catch (FirebaseMessagingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);

        }
        return "";
    }

    @Override
    public String unSubscribe( List<String> registration, String topic) {
        try {
            firebaseMessaging.unsubscribeFromTopic(registration, topic);
        } catch (FirebaseMessagingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return "";
    }

    @Override
    public boolean support(Class<?> type) {
        return false;
    }
}
