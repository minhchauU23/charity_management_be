package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String name);
    @Query(value = "SELECT role from RoleEntity role " +
            " WHERE role.name LIKE :searchKeyWord%"
    )
    Page<RoleEntity> search(@Param("searchKeyWord") String searchKeyWord, Pageable pageable);
}
