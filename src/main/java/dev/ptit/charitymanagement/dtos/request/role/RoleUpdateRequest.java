package dev.ptit.charitymanagement.dtos.request.role;

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
public class RoleUpdateRequest {
    @NotNull(message = "ROLE_NAME_INVALID")
    @NotBlank(message = "ROLE_NAME_INVALID")
    String name;
    String description;
}
