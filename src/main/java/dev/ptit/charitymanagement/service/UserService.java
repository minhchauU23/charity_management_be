package dev.ptit.charitymanagement.service;

import dev.ptit.charitymanagement.dtos.request.user.UserRequest;
import dev.ptit.charitymanagement.dtos.response.user.UserResponse;

public interface UserService extends CRUDService<UserResponse, UserRequest, Long> {
//    public UserResponse findByUsername(String username);
    public boolean changePassword(UserRequest request);
}
