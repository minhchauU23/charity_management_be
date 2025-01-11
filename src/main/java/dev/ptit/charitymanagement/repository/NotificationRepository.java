package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.NotificationEntity;
import dev.ptit.charitymanagement.entity.NotificationStatus;
import dev.ptit.charitymanagement.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {
    List<NotificationEntity> findByTypeAndCurrentStatus(NotificationType type, NotificationStatus currentStatus);
}
