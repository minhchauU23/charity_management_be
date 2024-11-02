package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.CampaignHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignHistoryRepository extends JpaRepository<CampaignHistory, String> {

    Optional<CampaignHistory> findFirstByCampaignIdOrderByUpdateAtDesc(String campaignId);
    List<CampaignHistory> findByCampaignIdOrderByUpdateAt(String campaignId);
}
