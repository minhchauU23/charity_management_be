package dev.ptit.charitymanagement.dtos.response.campaign;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import dev.ptit.charitymanagement.dtos.response.user.UserDTO;
import dev.ptit.charitymanagement.entity.CampaignStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignDTO {
    String id;
    List<String> images;
    String title;
    String shortDescription;
    String content;
    String story;
    String circumstances;
    Long fundraisingGoal;
    CampaignCategoryDTO category;
    UserDTO creator;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate endDate;
    @JsonFormat(pattern = "HH:mm")
    LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    LocalTime endTime;
    CampaignStatus currentStatus;
    Long totalAmountRaised; // Total amount raised for the campaign
    Long userAmountDonated;

//    Set<CampaignHistoryDTO> histories;
//    Set<Donation> donations;
}
