package dev.ptit.charitymanagement.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Formula;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tbl_campaign")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CampaignEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String title;
//    @Column(length = 1500)
//    List<String> images;
    Long fundraisingGoal;
    String shortDescription;
    @Column(columnDefinition = "TEXT")
    String content;
    LocalDate createdAt;
    LocalDate updateAt;
    LocalDate startDate;
    LocalDate endDate;
    @JsonFormat(pattern = "HH:mm")
    LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    LocalTime endTime;
    @Enumerated(EnumType.STRING)
    CampaignStatus currentStatus;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    UserEntity creator;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "campaign")
    Set<DonationEntity> donations;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    CampaignCategoryEntity category;
    @Formula("COALESCE((SELECT SUM(d.amount) FROM tbl_donation d WHERE d.campaign_id = id AND d.state = 1), 0)")
    Long totalAmountRaised; // Total amount raised for the campaign
    @Formula("COALESCE((SELECT COUNT(*) FROM tbl_donation d WHERE d.campaign_id = id AND d.state = 1), 0)")
    Long totalNumberDonations;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "campaign")
    Set<CampaignImageEntity> images;
    @OneToOne(mappedBy = "campaign", cascade = CascadeType.ALL)
    CampaignResultEntity result;
}
