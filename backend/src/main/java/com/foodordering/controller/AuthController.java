package com.foodordering.controller;

import com.foodordering.model.dto.GoogleLoginRequest;
import com.foodordering.model.dto.LoginRequest;
import com.foodordering.model.dto.RegisterRequest;
import com.foodordering.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 註冊
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    // 登入 (新增這個)
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // Google 登入
    @PostMapping("/google-login")
    public String googleLogin(@RequestBody GoogleLoginRequest request) {
        return authService.googleLogin(request.getIdToken());
    }
}