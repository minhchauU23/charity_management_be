package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, String> {
}
