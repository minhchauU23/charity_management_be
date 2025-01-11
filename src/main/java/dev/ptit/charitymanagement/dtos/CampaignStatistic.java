package dev.ptit.charitymanagement.dtos;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CampaignStatistic {
    Long totalStartedCampaign;
    List<Campaign> latestUpdateCampaigns;
}
