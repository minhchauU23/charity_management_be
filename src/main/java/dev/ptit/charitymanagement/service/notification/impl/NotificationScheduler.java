package dev.ptit.charitymanagement.service.notification.impl;

import dev.ptit.charitymanagement.dtos.Campaign;
import dev.ptit.charitymanagement.entity.*;
import dev.ptit.charitymanagement.repository.NotificationRepository;
import dev.ptit.charitymanagement.service.notification.NotificationSender;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class NotificationScheduler {
    EmailNotificationSender emailNotificationSender;
    NotificationRepository notificationRepository;

    @Scheduled(fixedRate = 10000)
    public void send() {
        List<NotificationEntity> notificationEntities = notificationRepository.findByTypeAndCurrentStatus(NotificationType.EMAIL, NotificationStatus.PENDING);
        log.info("Send email Scheduled running at {}", LocalDateTime.now());
        for(NotificationEntity notification: notificationEntities){
            emailNotificationSender.send(notification);
            notification.setUpdatedAt(LocalDateTime.now());
            notification.setCurrentStatus(NotificationStatus.SENDED);
            notificationRepository.save(notification);
        }
    }
}
