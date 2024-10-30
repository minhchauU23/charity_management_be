package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.request.campaign.CampaignCreationRequest;
import dev.ptit.charitymanagement.dtos.response.campaign.CampaignResponse;
import dev.ptit.charitymanagement.entity.User;
import dev.ptit.charitymanagement.service.campaigns.CampaignService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/campaigns")
public class CampaignsController {
    CampaignService campaignService;

    @GetMapping("/test")
    public ResponseEntity updateRegistration(HttpServletRequest request){
        campaignService.test();
//        notificationService.newNotificationTokenUser(userId, registration);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .build());
    }

    @PostMapping
    public ResponseEntity createCampaign(@RequestBody CampaignCreationRequest campaignCreationRequest, Authentication authentication, HttpServletRequest request){
        CampaignResponse response = campaignService.create(campaignCreationRequest, (User) authentication.getPrincipal());
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(response)
                .build());
    }

    @PutMapping
    public ResponseEntity updateCampaign(@RequestBody CampaignCreationRequest campaignCreationRequest, Authentication authentication, HttpServletRequest request){
        CampaignResponse response = campaignService.create(campaignCreationRequest, (User) authentication.getPrincipal());
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(response)
                .build());
    }

}
