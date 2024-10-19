package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.dtos.request.user.UserRequest;
import dev.ptit.charitymanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    void changePassword(User user);

    @Query(value = "SELECT user from User user JOIN FETCH user.userRoles ur JOIN FETCH ur.role WHERE user.email = :email")
    Optional<User> findUserWithRole(@Param("email") String email);
}
