package dev.ptit.charitymanagement.dtos.response.campaign;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import dev.ptit.charitymanagement.dtos.response.user.UserDTO;
import dev.ptit.charitymanagement.entity.CampaignStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
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
public class CampaignDTO {
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
    CampaignStatus currentStatus;
    UserDTO creator;
    CampaignCategoryDTO category;
//    Set<CampaignHistoryDTO> histories;
//    Set<Donation> donations;
}
