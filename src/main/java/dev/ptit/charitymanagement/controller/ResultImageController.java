package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.Image;
import dev.ptit.charitymanagement.service.image.S3CloudService;
import dev.ptit.charitymanagement.service.resultImage.ResultImageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/result-images")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResultImageController {
    ResultImageService resultImageService;
    @PostMapping
    public ResponseEntity create(@RequestBody Image image, HttpServletRequest request){
        Image res = resultImageService.create(image);
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(res)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") String id, Authentication authentication, HttpServletRequest request){
        resultImageService.deleteById(id, authentication);

        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .build());
    }
}
