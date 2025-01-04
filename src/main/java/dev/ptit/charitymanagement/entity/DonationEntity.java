package dev.ptit.charitymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_donation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DonationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String fullName;
    String email;
    Long amount;
    LocalDateTime createdAt;
    LocalDateTime updateAt;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    boolean isAnonymousDonation;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    UserEntity donor;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    CampaignEntity campaign;
    DonationState state;
}
