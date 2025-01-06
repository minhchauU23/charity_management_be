package dev.ptit.charitymanagement.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class User {
    Long id;
    String email;
    String password;
    String avatar;
    String firstName;
    String lastName;
    String phone;
    String address;
    String gender;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dob;
    boolean locked;
    List<Role> roles;
}
