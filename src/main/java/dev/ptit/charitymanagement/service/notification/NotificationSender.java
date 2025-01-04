package dev.ptit.charitymanagement.service.notification;

import dev.ptit.charitymanagement.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public interface NotificationSender {
    public void send(NotificationEntity notificationEntity);
    public boolean support(NotificationEntity notificationEntity);
}
