package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.CampaignResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignResultRepository extends JpaRepository<CampaignResultEntity, String> {
}
