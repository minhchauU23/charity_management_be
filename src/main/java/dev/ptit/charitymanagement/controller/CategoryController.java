package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.request.campaign.CampaignCategoryRequest;
import dev.ptit.charitymanagement.dtos.response.campaign.CampaignCategoryDTO;
import dev.ptit.charitymanagement.dtos.response.campaign.CampaignDTO;
import dev.ptit.charitymanagement.service.campaigns.CampaignCategoryService;
import dev.ptit.charitymanagement.service.campaigns.CampaignService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/categories")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryController {
    CampaignCategoryService campaignCategoryService;
    CampaignService campaignService;
    @PostMapping
    public ResponseEntity create(@RequestBody @Valid CampaignCategoryRequest campaignCategoryRequest, HttpServletRequest request){
        CampaignCategoryDTO response = campaignCategoryService.create(campaignCategoryRequest);
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
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody  @Valid CampaignCategoryRequest campaignCategoryRequest, HttpServletRequest request){
        CampaignCategoryDTO response = campaignCategoryService.update( id, campaignCategoryRequest);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(response)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable("id") Long id, HttpServletRequest request){
        CampaignCategoryDTO response = campaignCategoryService.findById( id);
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
    public ResponseEntity findAll(HttpServletRequest request){
        List<CampaignCategoryDTO> response = campaignCategoryService.findAll();
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(response)
                .build());
    }

    @GetMapping("/{id}/campaigns")
    public ResponseEntity getAllCampaignsOfCategory(@PathVariable("id") Long id, @RequestParam(defaultValue = "0") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    @RequestParam(defaultValue = "") String searchKeyWord,
                                                    @RequestParam(defaultValue = "id,asc") String sort,
                                                    HttpServletRequest request){

        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(campaignService.findAllByCategory(page, pageSize, searchKeyWord, sort, id))
                .build());
    }

}
