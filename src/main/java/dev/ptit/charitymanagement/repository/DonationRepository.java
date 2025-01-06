package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.CampaignStatus;
import dev.ptit.charitymanagement.entity.DonationEntity;
import dev.ptit.charitymanagement.entity.DonationState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<DonationEntity, String> {
    Page<DonationEntity> findByCampaignIdAndState(String campaignId, DonationState state, Pageable pageable);
    Page<DonationEntity> findByCampaignId(String campaignId, Pageable pageable);
    Page<DonationEntity> findByDonorId(Long donorId, Pageable pageable);

    @Query("SELECT COUNT(d) FROM DonationEntity d WHERE d.state = :state")
    Long countDonationsByState(@Param("state") DonationState state);

    // Get total amount of accepted donations
    @Query("SELECT SUM(d.amount) FROM DonationEntity d WHERE d.state = :state")
    Long getTotalAmountRaised(@Param("state") DonationState state);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM DonationEntity d " +
            "JOIN d.campaign c " +
            "WHERE c.currentStatus = 'ENDED' " +
            "AND d.state = 1")
    Long getTotalAmountDelivered();

    // Get latest donations ordered by creation date
    @Query("SELECT d FROM DonationEntity d ORDER BY d.createdAt DESC")
    List<DonationEntity> findLatestDonations(Pageable pageable);


}
