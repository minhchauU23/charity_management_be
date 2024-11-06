package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.request.role.RoleCreateRequest;
import dev.ptit.charitymanagement.dtos.request.role.RoleUpdateRequest;
import dev.ptit.charitymanagement.service.role.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping(("/roles"))
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @GetMapping
    public ResponseEntity findAll(HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                        .code(200)
                        .message("ok")
                        .time(new Date())
                        .endpoint(request.getRequestURI())
                        .method(request.getMethod())
                        .data(roleService.findAll())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable("id") Long id, HttpServletRequest request){
        return  ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(roleService.findById(id))
                .build()
        );
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid RoleCreateRequest roleCreateRequest, HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(roleService.create(roleCreateRequest))
                .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody @Valid RoleUpdateRequest roleUpdateRequest, HttpServletRequest request){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(roleService.update(id, roleUpdateRequest))
                .build()
        );
    }

    @GetMapping("/{id}/users")
    public ResponseEntity getUserOfRole( @PathVariable("id") Long roleId,
                                         @RequestParam(defaultValue = "0") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         @RequestParam(defaultValue = "") String searchKeyWord,
                                         @RequestParam(defaultValue = "id,asc") String sort,
                                         HttpServletRequest request
                                         ){
        return ResponseEntity.ok(APIResponse.builder()
                .code(200)
                .message("ok")
                .time(new Date())
                .endpoint(request.getRequestURI())
                .method(request.getMethod())
                .data(roleService.getUserOfRoles(roleId, page, pageSize, searchKeyWord, sort))
                .build()
        );
    }

}