package dev.ptit.charitymanagement.entity;

import dev.ptit.charitymanagement.dtos.Campaign;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "tbl_campaign_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CampaignResultEntity {
    @Id
    String id;
    LocalDate executeDate;
    String executeLocation;
    @Column(columnDefinition = "TEXT")
    String content;
    LocalDate createdAt;
    LocalDate updatedAt;
    @OneToOne
    @MapsId
    @JoinColumn(name = "id") // Sử dụng cột id làm foreign key
    CampaignEntity campaign;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "result")
    Set<ResultImageEntity> images;
}
