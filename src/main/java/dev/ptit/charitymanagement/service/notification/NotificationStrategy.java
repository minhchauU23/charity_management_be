package dev.ptit.charitymanagement.service.notification;

import dev.ptit.charitymanagement.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public interface NotificationStrategy {
    public void send(Notification notification);
    public boolean support(Class<?> notification);
}
