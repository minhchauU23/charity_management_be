package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.Campaign;
import dev.ptit.charitymanagement.entity.CampaignStatus;
import dev.ptit.charitymanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, String> {
    Page<Campaign> findByCurrentStatus(CampaignStatus currentStatus, Pageable pageable);
    Page<Campaign> findByCategoryId(Long categoryId, Pageable pageable);
    @Query(value = "SELECT * FROM tbl_campaign WHERE MATCH(name, short_description, full_description) "
            + "AGAINST (?1)", nativeQuery = true)
    Page<Campaign> searchBy(  @Param("status") CampaignStatus campaignStatus, @Param("searchKeyWord") String searchKeyWord, Pageable pageable);
    List<Campaign> findByCurrentStatusAndStartTime(CampaignStatus currentStatus, LocalDateTime startTime);
    List<Campaign> findByCurrentStatusAndEndTime(CampaignStatus currentStatus, LocalDateTime endTime);
//    List<Campaign> findByCurrentStatusAnd(CampaignStatus currentStatus, LocalDateTime endTime);
}
