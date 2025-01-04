package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.UserRoleEntity;
import dev.ptit.charitymanagement.entity.UserRoleCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleCompositeKey> {
    void deleteByUserId( Long userId);
}
