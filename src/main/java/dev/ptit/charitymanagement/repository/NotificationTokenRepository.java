package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.NotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTokenRepository extends JpaRepository<NotificationToken, String> {
    boolean existsByRegistrationAndUserId(String registration, Long userId);
    Optional<NotificationToken> findByRegistrationAndUserId(String registration, Long userId);

}
