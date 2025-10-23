package com.educacaofinanceira.controller;

import com.educacaofinanceira.dto.request.CreateChildRequest;
import com.educacaofinanceira.dto.response.UserResponse;
import com.educacaofinanceira.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/children")
    public ResponseEntity<UserResponse> createChild(@Valid @RequestBody CreateChildRequest request) {
        UserResponse child = userService.createChild(request);
        return ResponseEntity.ok(child);
    }

    @GetMapping("/children")
    public ResponseEntity<List<UserResponse>> getChildren() {
        List<UserResponse> children = userService.getChildren();
        return ResponseEntity.ok(children);
    }
}
