package dev.ptit.charitymanagement.dtos.request.auth;

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
public class ResetPasswordRequest {
    String email;
    String password;
    String repeatPassword;
    String resetPasswordCode;
}
