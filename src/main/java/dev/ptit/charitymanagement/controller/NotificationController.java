package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.service.notification.impl.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;
//    @PostMapping("/{userid}/firebase/registration")
//    @PreAuthorize("#userId == authentication.principal.id")
//    public ResponseEntity updateRegistration(@PathVariable("userid") Long userId, @RequestBody NotificationTokenRequest registration, HttpServletRequest request){
//        return ResponseEntity.ok(APIResponse.builder()
//                .code(200)
//                .message("ok")
//                .time(new Date())
//                .endpoint(request.getRequestURI())
//                .method(request.getMethod())
//                .build());
//    }


}
