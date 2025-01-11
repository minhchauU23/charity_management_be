package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.CampaignImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignImageRepository extends JpaRepository<CampaignImageEntity, String> {

}
