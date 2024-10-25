package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.request.notification.NotificationTokenRequest;
import dev.ptit.charitymanagement.entity.FirebaseNotification;
import dev.ptit.charitymanagement.entity.Notification;
import dev.ptit.charitymanagement.entity.NotificationToken;
import dev.ptit.charitymanagement.service.notification.NotificationService;
import dev.ptit.charitymanagement.service.notification.component.NotificationServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationServiceImpl notificationService;
    @PostMapping("/{userid}/firebase/registration")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity updateRegistration(@PathVariable("userid") Long userId, @RequestBody NotificationTokenRequest registration, HttpServletRequest request){
        notificationService.newNotificationTokenUser(userId, registration);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .build());
    }


}
