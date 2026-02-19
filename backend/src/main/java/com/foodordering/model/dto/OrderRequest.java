package com.foodordering.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    // 一筆訂單裡面會有很多個品項
    private List<OrderItemRequest> items;
}