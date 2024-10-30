package dev.ptit.charitymanagement.dtos.response.campaign;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.ptit.charitymanagement.entity.CampaignHistory;
import dev.ptit.charitymanagement.entity.Donation;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignResponse {
    String id;
    String title;
    List<String> images;
    Long fundraisingGoal;
    String shortDescription;
    String content;
    Date startDate;
    Date endDate;
    Set<CampaignHistoryResponse> histories;
//    Set<Donation> donations;
}
