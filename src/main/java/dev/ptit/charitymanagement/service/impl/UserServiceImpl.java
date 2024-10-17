package dev.ptit.charitymanagement.service.impl;

import dev.ptit.charitymanagement.dtos.request.user.UserRequest;
import dev.ptit.charitymanagement.dtos.response.user.UserResponse;
import dev.ptit.charitymanagement.entity.User;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.UserRepository;
import dev.ptit.charitymanagement.service.UserService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService, UserDetailsService {
    final UserRepository userRepository;

    @Override
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dob(user.getDob())
                .address(user.getAddress())
                .build();
    }

    public UserResponse getUserWithRole(Long id) {
        User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dob(user.getDob())
                .address(user.getAddress())
                .build();
    }

    @Override
    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dob(user.getDob())
                .address(user.getAddress())
                .build())
                .toList();
    }

    @Override
    public UserResponse create(UserRequest userRequest) {
        User user = User.builder()
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .gender(userRequest.getGender())
                .dob(userRequest.getDob())
                .address(userRequest.getAddress())
                .build();
        user = userRepository.save(user);
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dob(user.getDob())
                .address(user.getAddress())
                .build();
    }

    @Override
    public UserResponse update(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id).orElseThrow(() ->new RuntimeException("User not found"));
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setAddress(userRequest.getAddress());
        user.setGender(userRequest.getGender());
        user.setDob(userRequest.getDob());
        user = userRepository.save(user);
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dob(user.getDob())
                .address(user.getAddress())
                .build();
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->new RuntimeException("User not found"));
        user.setEnabled(false);
        user.setIsLocked(true);
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findUserWithRole(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }


}
