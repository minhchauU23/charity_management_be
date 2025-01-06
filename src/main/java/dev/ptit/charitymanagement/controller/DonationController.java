package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.Donation;
import dev.ptit.charitymanagement.entity.UserEntity;
import dev.ptit.charitymanagement.service.donation.DonationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DonationController {
    DonationService donationService;
    @PostMapping
    public ResponseEntity donate(@RequestBody Donation donation, Authentication authentication, HttpServletRequest request){
        donation.setIpAddress(request.getRemoteAddr());
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(donationService.donate(donation,(UserEntity) authentication.getPrincipal() ))
                .build());
    }

    @GetMapping
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity getAll( @RequestParam(defaultValue = "0") Integer page,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(donationService.getAll(page, pageSize))
                .build());
    }

    @GetMapping("/statistic")
    public ResponseEntity statistic(
                                  HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(donationService.statistic())
                .build());
    }


}
