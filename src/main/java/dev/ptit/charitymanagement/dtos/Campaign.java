package dev.ptit.charitymanagement.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class Campaign {
    String id;
    List<Image> images;
//    List<String> images;
    String title;
    String shortDescription;
    String content;
    String story;
    String circumstances;
    Long fundraisingGoal;
    CampaignCategory category;
    User creator ;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate endDate;
    @JsonFormat(pattern = "HH:mm")
    LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    LocalTime endTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate updateAt;
    CampaignStatus currentStatus;
    Long totalAmountRaised; // Total amount raised for the campaign
    Long totalNumberDonations;
    CampaignResult result;

//    Set<CampaignHistoryDTO> histories;
//    Set<Donation> donations;
}
