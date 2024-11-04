package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.UserRole;
import dev.ptit.charitymanagement.entity.UserRoleCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleCompositeKey> {
//    Optional<UserRole> findByRoleIdAndUserEmail(Long roleId, String email);
    void deleteByUserId( Long userId);
//    Optional<UserRole> findByRoleAndEmail(Long roleId, String email);
}
