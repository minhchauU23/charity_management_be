package dev.ptit.charitymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "tbl_result_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ResultImageEntity {
    @Id
    String id;
    String url;
    String fileName;
    String folder;
    String description;
    @ManyToOne(cascade = CascadeType.ALL)
    CampaignResultEntity result;
}
