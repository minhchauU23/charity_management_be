package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT user from UserEntity user JOIN FETCH user.userRoles ur JOIN FETCH ur.role WHERE user.email = :email")
    Optional<UserEntity> findByEmailWithRoles(@Param("email") String email);

    @Query(value = "SELECT user from UserEntity user JOIN FETCH user.userRoles ur JOIN FETCH ur.role WHERE user.id = :id")
    Optional<UserEntity> findByIdWithRoles(@Param("id") Long id);

    @Query(value = "SELECT user from UserEntity user")
    Page<UserEntity> findAllUser(Pageable pageable);

    @Query(value = "SELECT user from UserEntity user " +
            " WHERE user.email LIKE :searchKeyWord% OR user.firstName " +
            " LIKE :searchKeyWord% OR user.lastName LIKE :searchKeyWord% "
           )
    Page<UserEntity> search(@Param("searchKeyWord") String searchKeyWord, Pageable pageable);

    @Query(value = "SELECT user from UserEntity user JOIN FETCH user.userRoles ur JOIN FETCH ur.role role WHERE role.id = :roleId " +
            " AND (user.email LIKE :searchKeyWord% " +
            " OR user.firstName LIKE :searchKeyWord% " +
            " OR user.lastName LIKE :searchKeyWord%)")
    Page<UserEntity> findByRoleId(@Param("roleId") Long roleId, @Param("searchKeyWord") String searchKeyWord, Pageable pageable);

}
