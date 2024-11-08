package dev.ptit.charitymanagement.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Date;
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
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String title;
    List<String> images;
    Long fundraisingGoal;
    String shortDescription;
    String content;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime startTime;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime endTime;
    @Enumerated(EnumType.STRING)
    CampaignStatus currentStatus;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    User creator;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "campaign")
    Set<Donation> donations;
//    @OneToOne
//    CampaignResult result;
}
