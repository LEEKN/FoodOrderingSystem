package com.foodordering.service;

import com.foodordering.model.dto.LoginRequest;
import com.foodordering.model.dto.RegisterRequest;
import com.foodordering.model.entity.User;
import com.foodordering.model.utils.JwtUtil;
import com.foodordering.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// 匯入 Google 套件
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Collections;
import java.util.UUID;

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

    // 去 Google Cloud Console 申請這串 ID
    private static final String GOOGLE_CLIENT_ID = "894457800338-5crmbr1dmmkskgjum1vs4rahsop69mf4.apps.googleusercontent.com";

    // --- Google 登入 ---
    public String googleLogin(String idTokenString) {
        try {
            // 1. 設定 Google 驗證器
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            // 2. 驗證前端傳來的 Token
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new RuntimeException("Google 登入失敗：無效的 Token");
            }

            // 3. 解析出 Google 帳號資訊
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name"); // Google 上的名字

            // 4. 檢查資料庫有沒有這個信箱？
            // (我們假設 Google 登入的帳號，username 直接使用 email)
            User user = userRepository.findByUsername(email).orElse(null);

            if (user == null) {
                // 如果是新朋友，系統自動幫他註冊！
                user = new User();
                user.setUsername(email);
                user.setEmail(email);
                user.setRole("ROLE_USER");
                // 因為他不用打密碼，我們幫他產生一組無法被破解的亂碼當作密碼 (符合資料庫不可為空的規定)
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                userRepository.save(user);
            }

            // 5. 登入/註冊成功，核發我們系統自己的 JWT Token 給他
            return jwtUtil.generateToken(user.getUsername());

        } catch (Exception e) {
            throw new RuntimeException("Google 登入處理發生錯誤: " + e.getMessage());
        }
    }
}