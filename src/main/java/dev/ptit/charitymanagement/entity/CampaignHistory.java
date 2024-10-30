package dev.ptit.charitymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Table(name = "tbl_campaign_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CampaignHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    Campaign campaign;
    @Enumerated(EnumType.ORDINAL)
    CampaignStatus status;
    Date updateAt;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    User user;
}
