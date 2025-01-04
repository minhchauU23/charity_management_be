package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.dtos.NotificationTemplate;
import dev.ptit.charitymanagement.entity.NotificationTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplateEntity, Long> {
    Optional<NotificationTemplateEntity> findByName(String name);
}
