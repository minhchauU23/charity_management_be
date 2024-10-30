package dev.ptit.charitymanagement.dtos.request.campaign;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignCreationRequest {
    String title;
    List<String> images;
    Long fundraisingGoal;
    String shortDescription;
    String content;
    Date startDate;
    Date endDate;
}
