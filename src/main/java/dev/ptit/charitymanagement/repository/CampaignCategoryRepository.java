package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.CampaignCategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignCategoryRepository extends JpaRepository<CampaignCategoryEntity, Long> {
    @Query(value = "SELECT category from CampaignCategoryEntity category " +
            " WHERE category.name LIKE :searchKeyWord%"
    )
    Page<CampaignCategoryEntity> search(@Param("searchKeyWord") String searchKeyWord, Pageable pageable);
}
