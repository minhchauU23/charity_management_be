package dev.ptit.charitymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "tbl_campaign")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String fullName;
    String email;
    boolean isAnonymousDonation;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    User user;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    Campaign campaign;
}
