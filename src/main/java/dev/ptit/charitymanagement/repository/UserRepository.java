package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.dtos.request.user.UserRequest;
import dev.ptit.charitymanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    

    @Query(value = "SELECT user from User user JOIN FETCH user.userRoles ur JOIN FETCH ur.role WHERE user.email = :email")
    Optional<User> findUserWithRole(@Param("email") String email);


    @Query(value = "SELECT user from User user")
    Page<User> findAllUser( Pageable pageable);

    @Query(value = "SELECT user from User user " +
            " WHERE user.email LIKE :searchKeyWord% OR user.firstName " +
            " LIKE :searchKeyWord% OR user.lastName LIKE :searchKeyWord% "
           )
    Page<User> search(@Param("searchKeyWord") String searchKeyWord, Pageable pageable);


}
