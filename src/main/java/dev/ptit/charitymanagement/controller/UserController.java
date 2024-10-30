package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.request.user.*;
import dev.ptit.charitymanagement.service.reset_password.ResetPasswordService;
import dev.ptit.charitymanagement.service.reset_password.ResetPasswordServiceImpl;
import dev.ptit.charitymanagement.service.user.UserService;
import dev.ptit.charitymanagement.service.user.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EnableMethodSecurity
@RequiredArgsConstructor
public class UserController {
    ResetPasswordService resetPasswordService;
    UserServiceImpl userService;

    @PostMapping("/forgot_password")
    public  ResponseEntity forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest, HttpServletRequest request){
        resetPasswordService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok(APIResponse.builder()
                        .code(200)
                        .message("ok")
                        .time(new Date())
                        .endpoint(request.getRequestURI())
                        .method(request.getMethod())
                .build());
    }

    @PostMapping("/reset_password")
    public ResponseEntity resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest, HttpServletRequest request){
        resetPasswordService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .build());
    }

    @GetMapping
    public ResponseEntity findAllUser(@RequestParam(defaultValue = "0") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(defaultValue = "false") boolean isLocked,
                                      @RequestParam(defaultValue = "") String searchKeyWord,
                                      @RequestParam(defaultValue = "id,asc") String sort){
        return ResponseEntity.ok(userService.getAllUser(page, pageSize, searchKeyWord, sort));
    }

    @PostMapping

    public ResponseEntity create(@RequestBody UserRequest userRequest){
        return ResponseEntity.ok(userService.create(userRequest));
    }

    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity findById(@PathVariable("id") Long id){
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable("id") Long id, @RequestBody UserUpdateRequest userRequest){
        return ResponseEntity.ok(userService.update(id,userRequest));
    }


    @PatchMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity updateUserProfile(@PathVariable("id") Long id, @RequestBody UpdateProfileRequest updateProfileRequest){
        return ResponseEntity.ok(userService.updateProfile(id,updateProfileRequest));
    }

}
