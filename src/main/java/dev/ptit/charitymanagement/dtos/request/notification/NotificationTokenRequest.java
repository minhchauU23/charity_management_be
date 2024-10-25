package dev.ptit.charitymanagement.dtos.request.notification;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationTokenRequest {
    Long userId;
    String registration;

}
