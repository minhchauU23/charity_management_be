package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.ChangePasswordRequest;
import dev.ptit.charitymanagement.dtos.Image;
import dev.ptit.charitymanagement.dtos.User;
import dev.ptit.charitymanagement.service.campaigns.CampaignService;
import dev.ptit.charitymanagement.service.donation.DonationService;
import dev.ptit.charitymanagement.service.image.ImageService;
import dev.ptit.charitymanagement.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

    UserService userService;
    CampaignService campaignService;
    DonationService donationService;

    @GetMapping
    public ResponseEntity getAllUser(@RequestParam(defaultValue = "0") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(defaultValue = "") String[] filter,
                                      @RequestParam(defaultValue = "") String searchKeyWord,
                                      @RequestParam(defaultValue = "id,asc") String sort, HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(userService.findAll(page, pageSize, searchKeyWord, sort))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable("id") Long id, HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(userService.findById(id))
                .build());
    }

    @PutMapping("/{id}/avatar")
    @PreAuthorize(value = "#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity updateAvatar(@PathVariable("id") Long id,
                                                   @RequestBody Image image,
                                                   HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(userService.updateAvatar(id, image ))
                .build());
    }

    @PostMapping
    public ResponseEntity create(@RequestBody User createRequest, HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                        .code(200)
                        .message("ok")
                        .time(new Date())
                        .endpoint(request.getRequestURI())
                        .method(request.getMethod())
                        .data(userService.create(createRequest))
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody  User user, HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(userService.update(id,user))
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody  User registerRequest, HttpServletRequest request){
        userService.register(registerRequest);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .build());
    }

    @GetMapping("/{id}/profile")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity getProfile(@PathVariable("id") Long id, HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(userService.getProfile(id))
                .build());
    }

    @PutMapping("/{id}/profile")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity updateProfile(@PathVariable("id") Long id, @RequestBody User user, HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(userService.updateProfile(id,user))
                .build());
    }

    @PutMapping("/{id}/change-password")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity changePassword(@PathVariable("id") Long id, @RequestBody ChangePasswordRequest changePasswordRequest, HttpServletRequest request){
        userService.changePassword(id, changePasswordRequest);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .build());
    }

    @GetMapping("/{id}/campaigns")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity getCampaignOfUser(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @PathVariable("id") Long id, HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(campaignService.getCampaignOfUser(page, pageSize, id))
                .build());
    }

    @GetMapping("/{id}/donations")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity getDonationsOfUser(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @PathVariable("id") Long id, HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(donationService.getAllDonationOfUser(page, pageSize, id))
                .build());
    }

}
