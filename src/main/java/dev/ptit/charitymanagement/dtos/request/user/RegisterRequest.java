package dev.ptit.charitymanagement.dtos.request.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequest {
    @NotNull(message = "EMAIL_INVALID")
    @NotBlank(message = "EMAIL_INVALID")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "EMAIL_INVALID")
    String email;
    @NotBlank(message = "PASSWORD_INVALID")
    @Length(min = 8, message = "PASSWORD_INVALID")
    String password;
    @NotNull(message = "FIRST_NAME_INVALID" )
    @NotBlank(message = "FIRST_NAME_INVALID")
    String firstName;
    @NotNull(message = "LAST_NAME_INVALID")
    @NotBlank(message = "LAST_NAME_INVALID")
    String lastName;
    @NotNull(message = "PHONE_INVALID")
    @NotBlank(message = "PHONE_INVALID")
    @Pattern(regexp = "^\\+?[0-9\\s-]{7,15}$", message = "PHONE_INVALID")
    String phone;
    @NotNull(message = "DOB_INVALID")
    LocalDate dob;
//    String address;
//    String gender;
}
