package dev.ptit.charitymanagement.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.ptit.charitymanagement.dtos.response.auth.Token;
import dev.ptit.charitymanagement.dtos.response.user.UserDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResponse {
    Token token;
    User infor;
}
