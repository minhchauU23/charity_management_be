package dev.ptit.charitymanagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String registration;
    boolean isActive;
    @ManyToOne
    User user;
}
