package dev.ptit.charitymanagement.service.notification.component;

import dev.ptit.charitymanagement.entity.Notification;
import dev.ptit.charitymanagement.service.notification.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {
    RabbitTemplate template;
//    List<NotificationStrategy> notificationStrategies;
    //make routing send, its is publisher or producer
    List<String> queuesName = List.of("EMAIL", "PUSH");
    //RabbitMQTemplate.
    //
    @Override
    public void send(Notification notification) {
        for(String queue: queuesName){
            if(notification.getType().equals(queue)){
                template.convertAndSend(queue, notification);
                log.info("Sent to {}", queue);
            }
        }
//        for(NotificationStrategy notificationStrategy: notificationStrategies){
//            if(notificationStrategy.support(notification.getClass())){
//                notificationStrategy.send(notification);
//
//            }
//        }

//        String message =(String) template.receiveAndConvert("myqueue");
//        template.conver
//        System.out.println(message);
    }

    @Override
    public void sendAll(Notification notification) {

    }


}
