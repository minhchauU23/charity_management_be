package dev.ptit.charitymanagement.service.notification.component;

import dev.ptit.charitymanagement.entity.EmailNotification;
import dev.ptit.charitymanagement.entity.Notification;
import dev.ptit.charitymanagement.entity.PushNotification;
import dev.ptit.charitymanagement.service.notification.NotificationStrategy;
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
public class NotificationConsumer {
    List<NotificationStrategy> notificationStrategies;
    @RabbitListener(queues = "EMAIL")
    public void receiveEmailMessage(EmailNotification notification) {
       log.info("Received: {}",  notification);
       log.info("Type of notification {}", notification.getClass());
       for (NotificationStrategy notificationStrategy: notificationStrategies){
           log.info("");
           if(notificationStrategy.support(notification.getClass())){
               notificationStrategy.send(notification);
               log.info("Send to {} by {}", notification.getReceipt(), notificationStrategy.getClass());
           }
       }
    }

    @RabbitListener(queues = "PUSH")
    public void receivePushMessage(PushNotification notification) {
       log.info("Received: {} at push" + notification);

        for (NotificationStrategy notificationStrategy: notificationStrategies){
            if(notificationStrategy.support(notification.getClass())){
                notificationStrategy.send(notification);
                log.info("Send to {} by {}", notification.getReceipt(), notificationStrategy.getClass());
            }
        }
    }


}
