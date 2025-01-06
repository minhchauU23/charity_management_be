package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.Campaign;
import dev.ptit.charitymanagement.dtos.CampaignResult;
import dev.ptit.charitymanagement.entity.CampaignStatus;
import dev.ptit.charitymanagement.entity.UserEntity;
import dev.ptit.charitymanagement.service.campaigns.CampaignService;
import dev.ptit.charitymanagement.service.comment.CommentService;
import dev.ptit.charitymanagement.service.donation.DonationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/campaigns")
public class CampaignsController {
    CampaignService campaignService;
    DonationService donationService;
    CommentService commentService;


    @GetMapping
    public ResponseEntity getAll(@RequestParam(defaultValue = "0") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam(required = false) String state,
                                 @RequestParam(required = false) Long categoryId,
                                 HttpServletRequest request,
                                 Authentication authentication
    ) {
        Page<Campaign> campaigns = campaignService.getAll(page, pageSize, state, categoryId, authentication);

        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(campaigns)
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity search(@RequestParam(defaultValue = "0") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam(required = true) String keyword,
                                 @RequestParam(required = false) List<String> status,
                                 HttpServletRequest request,
                                 Authentication authentication
    ) {


        List<CampaignStatus> statusList;
        if (status == null || status.isEmpty()) {
            statusList = Arrays.asList(CampaignStatus.values());
        } else {
            statusList = status.stream()
                        .map(CampaignStatus::fromString)
                        .collect(Collectors.toList());

        }
        Page<Campaign> campaigns = campaignService.search(page, pageSize, keyword, statusList, authentication);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(campaigns)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable("id") String id, HttpServletRequest request, Authentication authentication){

        Campaign response = campaignService.getById(id, authentication);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(response)
                .build());
    }

    @PostMapping
    public ResponseEntity createCampaign(@RequestBody  Campaign campaignCreationRequest, Authentication authentication, HttpServletRequest request){
        log.info("At Campaign controller create");
        Campaign response = campaignService.create(campaignCreationRequest, (UserEntity) authentication.getPrincipal());
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
    public ResponseEntity updateDetail(@PathVariable("id") String id, @RequestBody   Campaign campaignUpdateRequest, Authentication authentication, HttpServletRequest request){
        Campaign response = campaignService.updateDetail(id,campaignUpdateRequest, (UserEntity) authentication.getPrincipal());
        log.info("Call at update");
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(response)
                .build());
    }

    @PutMapping("/{id}/schedule")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity schedule(@PathVariable("id") String id, @RequestBody   Campaign campaign, HttpServletRequest request){
        Campaign response = campaignService.schedule(id, campaign);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(response)
                .build());
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity reject(@PathVariable("id") String id, HttpServletRequest request){
        Campaign response = campaignService.reject(id);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(response)
                .build());
    }

    @PutMapping("/{id}/start")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity start(@PathVariable("id") String id,  @RequestBody   Campaign campaign, HttpServletRequest request){
        Campaign response = campaignService.start(id, campaign);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(response)
                .build());
    }

    @PutMapping("/{id}/stop")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity stop(@PathVariable("id") String id, HttpServletRequest request){
        Campaign response = campaignService.stop(id);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(response)
                .build());
    }

    @PutMapping("/{id}/restart")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity restart(@PathVariable("id") String id,  @RequestBody   Campaign campaign, HttpServletRequest request){
        Campaign response = campaignService.restart(id, campaign);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(response)
                .build());
    }

    @PutMapping("/{id}/result")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity result(@PathVariable("id") String id, @RequestBody CampaignResult result, HttpServletRequest request){
        Campaign response = campaignService.result(id, result);
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
    public  ResponseEntity getDonationOfCampaign(@PathVariable("id") String id,
                                                 @RequestParam(defaultValue = "0") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                                 @RequestParam(required = false) String state,
                                                 HttpServletRequest  request,
                                                 Authentication authentication
    ){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(donationService.getDonationOfCampaign( page, pageSize, id, state, authentication))
                .build());
    }


    @GetMapping("/{id}/comments")
    public  ResponseEntity getCommentOfCampaign(@PathVariable("id") String id,
                                                 @RequestParam(defaultValue = "0") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                                 HttpServletRequest  request,
                                                 Authentication authentication
    ){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(commentService.getByCampaignId( page, pageSize, id))
                .build());
    }


    @GetMapping("/statistic")
    public  ResponseEntity getCampaignStatistic(
                                                HttpServletRequest  request
    ){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(campaignService.getStatistic())
                .build());
    }

}
