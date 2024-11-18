package dev.ptit.charitymanagement.dtos.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import dev.ptit.charitymanagement.dtos.response.role.RoleDTO;
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
public class UserDTO {
    Long id;
    String username;
    String avatar;
    String email;
    String firstName;
    String lastName;
    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate dob;
    String address;
    String gender;
    String phone;
    Boolean locked;
    List<RoleDTO> roles;
}
