package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.request.campaign.CampaignCreationRequest;
import dev.ptit.charitymanagement.dtos.request.campaign.CampaignStatusRequest;
import dev.ptit.charitymanagement.dtos.request.campaign.CampaignUpdateRequest;
import dev.ptit.charitymanagement.dtos.response.campaign.CampaignDTO;
import dev.ptit.charitymanagement.entity.CampaignStatus;
import dev.ptit.charitymanagement.entity.User;
import dev.ptit.charitymanagement.service.campaigns.CampaignService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/campaigns")
public class CampaignsController {
    CampaignService campaignService;


    @PostMapping
    public ResponseEntity createCampaign(@RequestBody @Valid CampaignCreationRequest campaignCreationRequest, Authentication authentication, HttpServletRequest request){
        CampaignDTO response = campaignService.create(campaignCreationRequest, (User) authentication.getPrincipal());
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(response)
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity updateDetail(@PathVariable("id") String id, @RequestBody  @Valid CampaignUpdateRequest campaignUpdateRequest, Authentication authentication, HttpServletRequest request){
        CampaignDTO response = campaignService.updateDetail(id,campaignUpdateRequest, (User) authentication.getPrincipal());
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(response)
                .build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity changeStatus(@PathVariable("id") String id, @RequestBody  @Valid CampaignStatusRequest statusRequest, HttpServletRequest request){
        CampaignDTO response = campaignService.updateStatus(id, statusRequest);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(response)
                .build());
    }

    @GetMapping
    @PreAuthorize(value = "hasRole('ADMIN') or (#status == 'STARTED' or #status == 'ENDED')")
    public ResponseEntity getAll(@RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam(defaultValue = "false") String status,
                                     @RequestParam(defaultValue = "") String searchKeyWord,
                                     @RequestParam(defaultValue = "id,asc") String sort,
                                 HttpServletRequest request) {
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(campaignService.findAllByCurrentStatus(page, pageSize, searchKeyWord, sort, status))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable("id") String id, HttpServletRequest request){
        CampaignDTO response = campaignService.findById(id);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(response)
                .build());
    }

    @GetMapping("/{id}/donations")
    public  ResponseEntity getDonationOfCampaign(@PathVariable("id") String id){
        return ResponseEntity.ok("ok");
    }


}
