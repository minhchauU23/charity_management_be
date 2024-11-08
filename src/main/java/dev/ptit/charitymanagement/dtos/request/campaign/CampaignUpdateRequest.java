package dev.ptit.charitymanagement.dtos.request.campaign;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignUpdateRequest {
//    @NotNull
//    @NotBlank
//    String title;
////    @NotNull
////    @NotBlank
//    List<String> images;
//    @NotNull
//    @NotBlank
//    Long fundraisingGoal;
//    @NotNull
//    @NotBlank
//    String shortDescription;
//    @NotNull
//    @NotBlank
//    String content;
    @NotNull(message = "CAMPAIGN_TITLE_INVALID")
    @NotBlank(message = "CAMPAIGN_TITLE_INVALID")
    String title;
    List<String> images;
    @NotNull(message = "CAMPAIGN_FUNDRAISING_GOAL_INVALID")
    Long fundraisingGoal;
    @NotNull(message = "CAMPAIGN_SHORT_DESCRIPTION_INVALID")
    @NotBlank(message = "CAMPAIGN_SHORT_DESCRIPTION_INVALID")
    String shortDescription;
    @NotNull(message = "CAMPAIGN_CONTENT_INVALID")
    @NotBlank(message = "CAMPAIGN_CONTENT_INVALID")
    String content;
//    @NotNull(message = "CAMPAIGN_START_TIME_INVALID")
//    LocalDateTime startTime;
//    @NotNull(message = "CAMPAIGN_END_TIME_INVALID")
//    LocalDateTime endTime;
//    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
//    LocalDateTime startTime;
//    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
//    LocalDateTime endTime;
}
