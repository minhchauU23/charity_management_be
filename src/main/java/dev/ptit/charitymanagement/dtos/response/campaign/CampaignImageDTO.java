package dev.ptit.charitymanagement.dtos.response.campaign;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignImageDTO {
    Long id;
    String preSignedUrl;
    String url;
    String description;
}
