package com.foodordering.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 餐點名稱

    private String description; // 描述

    @Column(nullable = false)
    private BigDecimal price; // 價格 (用 BigDecimal 計算金額才精準)

    private String category; // 分類 (例如：主餐、飲料)

    private String imageUrl; // 圖片網址 (之後前端顯示用)

    private Boolean available = true; // 是否上架中
}