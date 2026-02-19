package com.foodordering.model.dto;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long menuId;    // 客人點的餐點 ID (例如：1號餐)
    private Integer quantity; // 客人點的數量 (例如：2份)
}