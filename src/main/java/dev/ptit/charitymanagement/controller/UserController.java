package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.request.user.ForgotPasswordRequest;
import dev.ptit.charitymanagement.dtos.request.user.ResetPasswordRequest;
import dev.ptit.charitymanagement.service.reset_password.ResetPasswordService;
import dev.ptit.charitymanagement.service.reset_password.ResetPasswordServiceImpl;
import dev.ptit.charitymanagement.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserController {
    ResetPasswordService resetPasswordService;
    @GetMapping("/test")
    public ResponseEntity test(){
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/forgot_password")
    public  ResponseEntity forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest, HttpServletRequest request){
        resetPasswordService.forgotPassword(forgotPasswordRequest);
//        resetPasswordService.createResetCode(forgotPasswordRequest.getEmail());
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

}
