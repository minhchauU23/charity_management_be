package dev.ptit.charitymanagement.dtos.request.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequest {
    @NotNull
    String email;
    @NotNull
    @Size(min = 8)
    String password;
    @NotNull
    String firstName;
    @NotNull
    String lastName;
    @NotNull
    String phone;
//    LocalDate dob;
//    String address;
//    String gender;
}
