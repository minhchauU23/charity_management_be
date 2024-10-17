package dev.ptit.charitymanagement.dtos.request.user;


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
public class UserRequest {
    String email;
    String password;
    String firstName;
    String lastName;
    LocalDate dob;
    String phone;
    String address;
    String gender;
    Boolean isLocked;
    Boolean isEnabled;
}
