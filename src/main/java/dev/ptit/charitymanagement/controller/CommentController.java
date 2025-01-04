package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.Comment;
import dev.ptit.charitymanagement.dtos.Donation;
import dev.ptit.charitymanagement.entity.UserEntity;
import dev.ptit.charitymanagement.service.comment.CommentService;
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
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;
    @PostMapping
    public ResponseEntity createComment(@RequestBody Comment comment, Authentication authentication, HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(commentService.createComment(comment,(UserEntity) authentication.getPrincipal() ))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity updateComment(@PathVariable("id") String id, @RequestBody Comment comment, Authentication authentication, HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(commentService.updateComment(id,comment,(UserEntity) authentication.getPrincipal() ))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteComment(@PathVariable("id") String id, Authentication authentication, HttpServletRequest request){
        commentService.deleteComment(id, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .build());
    }
}
