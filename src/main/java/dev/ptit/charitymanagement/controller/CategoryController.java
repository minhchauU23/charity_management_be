package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.CampaignCategory;
import dev.ptit.charitymanagement.service.campaigns.CampaignCategoryService;
import dev.ptit.charitymanagement.service.campaigns.CampaignService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/categories")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryController {
    CampaignCategoryService campaignCategoryService;
    CampaignService campaignService;

    @GetMapping
    public ResponseEntity getAll(@RequestParam(defaultValue = "0") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam(defaultValue = "") String[] filter,
                                 @RequestParam(defaultValue = "") String searchKeyWord,
                                 @RequestParam(defaultValue = "id,asc") String sort,
                                 HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(campaignCategoryService.findAll(page, pageSize, searchKeyWord, sort))
                .build());
    }

    @PostMapping
    public ResponseEntity create(@RequestBody  CampaignCategory campaignCategoryRequest, HttpServletRequest request){
        CampaignCategory response = campaignCategoryService.create(campaignCategoryRequest);
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
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody   CampaignCategory campaignCategoryRequest, HttpServletRequest request){
        CampaignCategory response = campaignCategoryService.update( id, campaignCategoryRequest);
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
        CampaignCategory response = campaignCategoryService.findById( id);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(response)
                .build());
    }


//    @GetMapping("/{id}/campaigns")
//    public ResponseEntity getAllCampaignsOfCategory(@PathVariable("id") Long id, @RequestParam(defaultValue = "0") Integer page,
//                                                    @RequestParam(defaultValue = "10") Integer pageSize,
//                                                    @RequestParam(defaultValue = "") String searchKeyWord,
//                                                    @RequestParam(defaultValue = "id,asc") String sort,
//                                                    HttpServletRequest request){
//
//        return ResponseEntity.ok(APIResponse.builder()
//                .code(200)
//                .message("ok")
//                .time(new Date())
//                .endpoint(request.getRequestURI())
//                .method(request.getMethod())
//                .data(campaignService.findAllByCategory(page, pageSize, searchKeyWord, sort, id))
//                .build());
//    }

}
