package dev.ptit.charitymanagement.dtos.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.ptit.charitymanagement.dtos.response.role.RoleResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    Long id;
    String username;
    String email;
    String firstName;
    String lastName;
    LocalDate dob;
    String address;
    String gender;
    Boolean isLocked;
    Boolean isEnabled;
    List<RoleResponse> roles;
}
