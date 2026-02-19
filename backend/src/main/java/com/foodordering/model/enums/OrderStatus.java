package com.foodordering.model.enums;

public enum OrderStatus {
    PENDING,    // 待處理 (剛下單)
    PREPARING,  // 準備中 (廚房正在做)
    COMPLETED,  // 已完成 (出餐)
    CANCELLED   // 已取消
}