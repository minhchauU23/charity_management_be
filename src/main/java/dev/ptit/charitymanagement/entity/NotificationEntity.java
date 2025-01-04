package dev.ptit.charitymanagement.entity;

import dev.ptit.charitymanagement.dtos.NotificationTemplate;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_notification")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public  class NotificationEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    @Column(columnDefinition = "TEXT")
    String content;
    String data;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    NotificationTemplateEntity template;
    NotificationType type;
    NotificationStatus currentStatus;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    UserEntity destination;
}
