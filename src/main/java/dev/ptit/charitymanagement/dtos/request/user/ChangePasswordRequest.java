package dev.ptit.charitymanagement.dtos.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {
    @NotBlank(message = "PASSWORD_INVALID")
    @Length(min = 8, message = "PASSWORD_INVALID")
    String oldPassword;
    @NotBlank(message = "PASSWORD_INVALID")
    @Length(min = 8, message = "PASSWORD_INVALID")
    String newPassword;
    @NotBlank(message = "PASSWORD_INVALID")
    @Length(min = 8, message = "PASSWORD_INVALID")
    String repeatNewPassword;
}
