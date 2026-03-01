package com.foodordering.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    // 一筆訂單裡面會有很多個品項
    private List<OrderItemRequest> items;

    // 前端傳來的優惠碼 (非必填，客人不一定有優惠券)
    private String couponCode;
}