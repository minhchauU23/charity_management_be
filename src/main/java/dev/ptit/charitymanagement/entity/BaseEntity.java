package dev.ptit.charitymanagement.entity;

import dev.ptit.charitymanagement.component.BaseAuditingListener;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@MappedSuperclass
@EntityListeners(BaseAuditingListener.class)
public abstract class BaseEntity {
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    boolean enabled;
}
