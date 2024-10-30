package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByRoleIdAndUserEmail(Long roleId, String email);
//    Optional<UserRole> findByRoleAndEmail(Long roleId, String email);
}
