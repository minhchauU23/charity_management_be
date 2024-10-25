package dev.ptit.charitymanagement.service.notification.component;

import dev.ptit.charitymanagement.entity.EmailNotification;
import dev.ptit.charitymanagement.service.notification.NotificationSender;
import dev.ptit.charitymanagement.service.notification.NotificationSubscriber;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class NotificationSubscriberConsumer {
    RabbitTemplate rabbitTemplate;
    List<NotificationSubscriber> notificationSubscribers;

//    @RabbitListener(queues = "firebase_subsciber")
    public void receiveEmailMessage(Message message) {
        log.info("Received: {}",  message);
        for(NotificationSubscriber subscriber: notificationSubscribers){
            if(subscriber.support(message.getClass())){
//                subscriber.subscribe()
            }
        }
    }
}
