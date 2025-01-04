package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.DonationEntity;
import dev.ptit.charitymanagement.entity.DonationState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<DonationEntity, String> {
    Page<DonationEntity> findByCampaignIdAndState(String campaignId, DonationState state, Pageable pageable);
    Page<DonationEntity> findByCampaignId(String campaignId, Pageable pageable);
    Page<DonationEntity> findByDonorId(Long donorId, Pageable pageable);
}
