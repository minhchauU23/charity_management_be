package dev.ptit.charitymanagement.component;

import dev.ptit.charitymanagement.entity.User;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserAuditingListener {
    @PrePersist
    void prePersist(User user){
        user.setIsLocked(false);
        user.setEnabled(true);
        user.setIsLocked(false);
        user.setCreatedAt(LocalDateTime.now());
    }

    @PreUpdate
    void preUpdate(User user){
        user.setUpdatedAt(LocalDateTime.now());
    }
}
