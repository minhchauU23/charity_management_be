package dev.ptit.charitymanagement.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResetPasswordRequest {
    @NotNull(message = "EMAIL_INVALID")
    @NotBlank(message = "EMAIL_INVALID")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "EMAIL_INVALID")
    String email;
    @NotBlank(message = "PASSWORD_INVALID")
    @Length(min = 8, message = "PASSWORD_INVALID")
    String password;
    @NotBlank(message = "REPEAT_PASSWORD_NOT_MATCHING")
    @Length(min = 8, message = "REPEAT_PASSWORD_NOT_MATCHING")
    String repeatPassword;
    @NotNull(message = "RESET_PASSWORD_CODE_INVALID")
    @NotBlank(message = "RESET_PASSWORD_CODE_INVALID")
    String resetPasswordCode;
}
