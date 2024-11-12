package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.CampaignCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignCategoryRepository extends JpaRepository<CampaignCategory, Long> {

}
