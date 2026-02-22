package com.foodordering.controller;

import com.foodordering.model.dto.OrderRequest;
import com.foodordering.model.entity.Order;
import com.foodordering.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // --- 1. 新增訂單 (點餐) ---
    // 這支 API 會被 SecurityConfig 保護，必須帶 Token 才能呼叫
    @PostMapping
    public Order createOrder(@RequestBody OrderRequest request) {

        // 關鍵魔法：從 SecurityContext (安全上下文) 取得目前登入的使用者資訊
        // 這樣前端就不需要把 user_id 寫在 JSON 裡面傳過來，更安全！
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // 拿到 JWT 裡面解析出來的帳號

        // 呼叫 Service 建立訂單
        return orderService.createOrder(username, request);
    }
}