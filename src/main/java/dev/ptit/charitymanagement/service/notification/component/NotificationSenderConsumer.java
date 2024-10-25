package dev.ptit.charitymanagement.service.notification.component;

import dev.ptit.charitymanagement.entity.EmailNotification;
import dev.ptit.charitymanagement.entity.FirebaseNotification;
import dev.ptit.charitymanagement.service.notification.NotificationSender;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationSenderConsumer {
    List<NotificationSender> notificationStrategies;
    @RabbitListener(queues = "EMAIL")
    public void receiveEmailMessage(EmailNotification notification) {
       log.info("Received: {}",  notification);
       log.info("Type of notification {}", notification.getClass());
       for (NotificationSender notificationSender : notificationStrategies){
           log.info("");
           if(notificationSender.support(notification.getClass())){
               notificationSender.send(notification);
               log.info("Send to {} by {}", notification.getReceipt(), notificationSender.getClass());
           }
       }
    }

    @RabbitListener(queues = "FIREBASE")
    public void receivePushMessage(FirebaseNotification notification) {
       log.info("Firebase received: {}", notification);
        for (NotificationSender notificationSender : notificationStrategies){
            if(notificationSender.support(notification.getClass())){
                notificationSender.send(notification);
                log.info("Send to {} by {}", notification.getReceipt(), notificationSender.getClass());
            }
        }
    }


}
