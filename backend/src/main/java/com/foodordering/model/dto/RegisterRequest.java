package com.foodordering.model.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    // 這些是前端註冊時必須填寫的欄位
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
}