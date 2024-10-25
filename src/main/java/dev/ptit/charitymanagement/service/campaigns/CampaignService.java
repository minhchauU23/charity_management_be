package dev.ptit.charitymanagement.service.campaigns;

import dev.ptit.charitymanagement.dtos.request.notification.NotificationTokenRequest;
import dev.ptit.charitymanagement.entity.FirebaseNotification;
import dev.ptit.charitymanagement.entity.Notification;
import dev.ptit.charitymanagement.entity.NotificationToken;
import dev.ptit.charitymanagement.repository.NotificationTokenRepository;
import dev.ptit.charitymanagement.service.notification.NotificationService;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CampaignService {
    NotificationService notificationService;
    NotificationTokenRepository notificationTokenRepository;
    public void test(){
        List<NotificationToken> tokens = notificationTokenRepository.findAll();
        for(NotificationToken token : tokens){
            Notification notification = new FirebaseNotification();
            notification.setTitle("Test");
            notification.setType("FIREBASE");
            notification.setContent("Test content");
            notification.setReceipt(token.getRegistration());
            notificationService.send(notification);
        }
    }
}
