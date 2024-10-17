package dev.ptit.charitymanagement.component;

import dev.ptit.charitymanagement.entity.BaseEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BaseAuditingListener {
    @PrePersist
    void prePersist(BaseEntity entity){
        entity.setCreatedAt(LocalDateTime.now());
    }

    @PreUpdate
    void preUpdate(BaseEntity entity){
        entity.setUpdatedAt(LocalDateTime.now());
    }
}
