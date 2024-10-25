package dev.ptit.charitymanagement.service.notification;

import dev.ptit.charitymanagement.dtos.request.notification.NotificationTokenRequest;
import dev.ptit.charitymanagement.entity.Notification;

public interface NotificationService {
    public void send(Notification notification);
    public void sendAll(Notification notification);
    public void createNotificationToken(Long userId,NotificationTokenRequest request);
}
