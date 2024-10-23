package dev.ptit.charitymanagement.service.notification.component;

import dev.ptit.charitymanagement.entity.EmailNotification;
import dev.ptit.charitymanagement.entity.Notification;
import dev.ptit.charitymanagement.service.notification.NotificationStrategy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailNotificationStrategy implements NotificationStrategy {
    JavaMailSender javaMailSender;

    @Override
    public void send(Notification notification) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject(notification.getTitle());
        mailMessage.setTo(notification.getReceipt());
        mailMessage.setText(notification.getContent());
        javaMailSender.send(mailMessage);
    }

    @Override
    public boolean support(Class<?> notification) {
        return EmailNotification.class.isAssignableFrom(notification);
    }
}
