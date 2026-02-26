package com.foodordering.model.dto;

import lombok.Data;

@Data
public class GoogleLoginRequest {
    private String idToken; // 前端從 Google 那邊拿到的憑證
}