package dev.ptit.charitymanagement.service.notification.impl;

import dev.ptit.charitymanagement.entity.NotificationEntity;
import dev.ptit.charitymanagement.entity.NotificationStatus;
import dev.ptit.charitymanagement.entity.NotificationType;
import dev.ptit.charitymanagement.service.notification.NotificationSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailNotificationSender implements NotificationSender {
    JavaMailSender javaMailSender;

    @Override
    public void send(NotificationEntity notificationEntity) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setSubject("Thông báo từ [GiveWellVN]");
            helper.setTo(notificationEntity.getDestination().getEmail());
            helper.setText(notificationEntity.getContent(), true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean support(NotificationEntity notificationEntity) {
        return notificationEntity.getType().equals(NotificationType.EMAIL);
    }
}
