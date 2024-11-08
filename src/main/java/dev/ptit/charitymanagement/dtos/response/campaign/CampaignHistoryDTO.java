package dev.ptit.charitymanagement.dtos.response.campaign;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.ptit.charitymanagement.entity.CampaignStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignHistoryDTO {
    String id;
    CampaignStatus status;
    Date updateAt;
}
