package dev.ptit.charitymanagement.repository;

import dev.ptit.charitymanagement.entity.CampaignEntity;
import dev.ptit.charitymanagement.entity.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CampaignRepository extends JpaRepository<CampaignEntity, String>, JpaSpecificationExecutor<CampaignEntity> {
    @Query("SELECT c FROM CampaignEntity c WHERE c.currentStatus = :currentStatus ")
    Page<CampaignEntity> findByCurrentStatus(CampaignStatus currentStatus, Pageable pageable);


    @Query("SELECT c FROM CampaignEntity c WHERE c.currentStatus = :currentStatus AND c.category.id = :categoryId")
    Page<CampaignEntity> findByCurrentStatusAndCategoryId(@Param("currentStatus") CampaignStatus currentStatus,
                                                          @Param("categoryId") Long categoryId, Pageable pageable);

//    @Query(value = """
//    SELECT c.*,
//        COUNT(CASE WHEN d.state = 1 THEN 1 END) as  totalNumberDonations,
//        COALESCE(SUM(CASE WHEN d.state = 1 THEN d.amount ELSE 0 END), 0) as totalAmountRaised
//    FROM tbl_campaign c
//    LEFT JOIN tbl_donation d ON c.id = d.campaign_id
//    WHERE c.current_status = :currentStatus
//    AND c.category_id = :categoryId
//    GROUP BY c.id
//    """,
//            nativeQuery = true)
//    Page<CampaignEntity> findByCurrentStatusAndCategoryId(
//            @Param("currentStatus") String currentStatus,
//            @Param("categoryId") Long categoryId,
//            Pageable pageable
//    );

    Page<CampaignEntity> findByCategoryId(Long categoryId, Pageable pageable);

//    @Query("SELECT DISTINCT c FROM CampaignEntity c " +
//            "LEFT JOIN FETCH c.donations donation " +
//            "WHERE c.creator.id = :userId"
//    )
    @Query("SELECT c FROM CampaignEntity c WHERE c.creator.id = :userId")
    Page<CampaignEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
        SELECT DISTINCT c FROM CampaignEntity c 
        LEFT JOIN FETCH c.donations donation 
        WHERE c.currentStatus IN :statuses
        AND (
            LOWER(c.title) LIKE LOWER(CONCAT( :keyword, '%'))
            OR LOWER(c.shortDescription) LIKE LOWER(CONCAT( :keyword, '%'))
            OR LOWER(c.content) LIKE LOWER(CONCAT( :keyword, '%'))
        )
        """)
    Page<CampaignEntity> search(@Param("statuses") List<CampaignStatus> campaignStatuses, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(c) FROM CampaignEntity c WHERE c.currentStatus = :status")
    Long countCampaignsByStatus(@Param("status") CampaignStatus status);


    @Query("SELECT c FROM CampaignEntity c ORDER BY c.updateAt DESC")
    List<CampaignEntity> getLastUpdateCampaign(Pageable pageable);

    List<CampaignEntity> findByCurrentStatusAndStartDateAndStartTime(CampaignStatus currentStatus, LocalDate startDate, LocalTime startTime);
    List<CampaignEntity> findByCurrentStatusAndEndDateAndEndTime(CampaignStatus currentStatus, LocalDate endDate, LocalTime endTime);
}
