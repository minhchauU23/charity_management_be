package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.CommentEntity;
import dev.ptit.charitymanagement.entity.DonationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, String> {
    Page<CommentEntity> findByCampaignId(String campaignId, Pageable pageable);
}
