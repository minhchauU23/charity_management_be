package dev.ptit.charitymanagement.dtos.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class RegisterResponse {
    Long id;
    String email;
    String firstName;
    String lastName;
    LocalDate dob;
    String address;
    String gender;
}
