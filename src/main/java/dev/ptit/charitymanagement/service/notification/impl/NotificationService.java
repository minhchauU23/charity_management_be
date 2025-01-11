package dev.ptit.charitymanagement.service.notification.impl;

import dev.ptit.charitymanagement.dtos.Notification;
import dev.ptit.charitymanagement.dtos.NotificationTemplate;
import dev.ptit.charitymanagement.dtos.User;
import dev.ptit.charitymanagement.entity.NotificationEntity;
import dev.ptit.charitymanagement.entity.NotificationStatus;
import dev.ptit.charitymanagement.entity.NotificationTemplateEntity;
import dev.ptit.charitymanagement.entity.UserEntity;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.NotificationRepository;
import dev.ptit.charitymanagement.repository.NotificationTemplateRepository;
import dev.ptit.charitymanagement.repository.UserRepository;
import dev.ptit.charitymanagement.service.notificationTemplate.NotificationTemplateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    UserRepository userRepository;
    NotificationTemplateRepository notificationTemplateRepository;
    NotificationTemplateService notificationTemplateService;
    NotificationRepository notificationRepository;
    RabbitTemplate template;
    List<String> queuesName = List.of("EMAIL", "FIREBASE");

    public Notification create(Notification notification){
        UserEntity userEntity = userRepository.findById(notification.getDestination().getId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        NotificationTemplateEntity templateEntity = notificationTemplateRepository.findByName(notification.getTemplate().getName()).orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_TEMPLATE_NOT_EXISTED));
        String content = notificationTemplateService.generateContent(notification.getData(), templateEntity);

        NotificationEntity notificationEntity = NotificationEntity.builder()
                .type(notification.getType())
                .currentStatus(NotificationStatus.PENDING)
                .template(templateEntity)
                .data(notification.getData())
                .destination(userEntity)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
        notificationEntity = notificationRepository.save(notificationEntity);
        return Notification.builder()
                .id(notificationEntity.getId())
                .type(notificationEntity.getType())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .destination(User.builder()
                        .id(notificationEntity.getDestination().getId())
                        .email(notificationEntity.getDestination().getEmail())
                        .firstName(notificationEntity.getDestination().getFirstName())
                        .lastName(notificationEntity.getDestination().getLastName())
                        .build())
                .currentStatus(notificationEntity.getCurrentStatus())
                .build();
    }

    public Page<Notification> getAll(Integer page, Integer pageSize){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        return notificationRepository.findAll(pageable).map(notificationEntity ->  Notification.builder()
                .id(notificationEntity.getId())
                .currentStatus(notificationEntity.getCurrentStatus())
                .type(notificationEntity.getType())
                .destination(User.builder()
                        .email(notificationEntity.getDestination().getEmail())
                        .build())
                .template(NotificationTemplate.builder()
                        .name(notificationEntity.getTemplate().getName())
                        .build())
                .build());
    }



}
