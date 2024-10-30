package dev.ptit.charitymanagement.dtos.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import dev.ptit.charitymanagement.dtos.response.role.RoleResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

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
    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate dob;
    String address;
    String gender;
    String phone;
    Boolean isLocked;
    Boolean isEnabled;
    List<RoleResponse> roles;
}
