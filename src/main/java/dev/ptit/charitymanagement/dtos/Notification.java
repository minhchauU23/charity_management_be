package dev.ptit.charitymanagement.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.ptit.charitymanagement.entity.NotificationStatus;
import dev.ptit.charitymanagement.entity.NotificationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notification {
    String id;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String content;
    String data;
    NotificationType type;
    NotificationStatus currentStatus;
    User destination;
    NotificationTemplate template;

}
