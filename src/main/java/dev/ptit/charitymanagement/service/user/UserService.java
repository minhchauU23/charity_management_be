package dev.ptit.charitymanagement.service.user;

import dev.ptit.charitymanagement.dtos.request.user.UserRequest;
import dev.ptit.charitymanagement.dtos.response.user.UserResponse;
import dev.ptit.charitymanagement.service.CRUDService;

public interface UserService extends CRUDService<UserResponse, UserRequest, Long> {
//    public UserResponse findByUsername(String username);
//    public String changePassword(ResetPasswordRequest request);
}
