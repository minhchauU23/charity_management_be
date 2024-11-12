package dev.ptit.charitymanagement.dtos.request.campaign;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignCategoryRequest {
    @NotNull(message = "CATEGORY_NAME_INVALID")
    @NotBlank(message = "CATEGORY_NAME_INVALID")
    String name;
}
