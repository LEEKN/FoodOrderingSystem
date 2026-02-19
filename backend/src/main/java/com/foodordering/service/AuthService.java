package com.foodordering.service;

import com.foodordering.model.dto.LoginRequest;
import com.foodordering.model.dto.RegisterRequest;
import com.foodordering.model.entity.User;
import com.foodordering.model.utils.JwtUtil;
import com.foodordering.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil; // 注入剛剛寫的工具

    // --- 註冊 ---
    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("錯誤: 帳號已經被註冊過了！");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole("ROLE_USER");
        // 密碼加密
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        return "註冊成功！";
    }

    // --- 登入 ---
    public String login(LoginRequest request) {
        // 1. 找找看有沒有這個人
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("登入失敗：找不到此帳號");
        }

        User user = userOptional.get();

        // 2. 檢查密碼 (將使用者輸入的明碼，跟資料庫的加密亂碼比對)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("登入失敗：密碼錯誤");
        }

        // 3. 帳號密碼都對了，發 Token
        return jwtUtil.generateToken(user.getUsername());
    }
}