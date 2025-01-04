package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.NotificationTemplate;
import dev.ptit.charitymanagement.service.notificationTemplate.NotificationTemplateService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/notification-templates")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationTemplateController {
    NotificationTemplateService notificationTemplateService;

    @PostMapping
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity create(@RequestBody NotificationTemplate notificationTemplate, HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(notificationTemplateService.create(notificationTemplate))
                .build());
    }
    @GetMapping
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity getAll(@RequestParam(defaultValue = "0") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(notificationTemplateService.getAll(page, pageSize))
                .build());
    }
    @GetMapping("/{id}")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity getById(@PathVariable("id") Long id,
                                 HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(notificationTemplateService.getById(id))
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody NotificationTemplate template,
                                  HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(notificationTemplateService.update(id, template))
                .build());
    }


}
