package dev.ptit.charitymanagement.dtos.request.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProfileRequest {
    @NotNull(message = "EMAIL_INVALID")
    @NotBlank(message = "EMAIL_INVALID")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "EMAIL_INVALID")
    String email;
    @NotNull(message = "FIRST_NAME_INVALID" )
    @NotBlank(message = "FIRST_NAME_INVALID")
    String firstName;
    @NotNull(message = "LAST_NAME_INVALID")
    @NotBlank(message = "LAST_NAME_INVALID")
    String lastName;
    @NotNull(message = "PHONE_INVALID")
    @NotBlank(message = "PHONE_INVALID")
    @Pattern(regexp = "^\\+?[0-9\\s-]{7,15}$")
    String phone;
    @NotNull(message = "ADDRESS_INVALID")
    String address;
    @NotNull(message = "GENDER_INVALID")
    String gender;
    @NotNull(message = "DOB_INVALID")
    LocalDate dob;
}
