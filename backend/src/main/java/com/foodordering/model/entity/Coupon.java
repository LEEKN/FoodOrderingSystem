package com.foodordering.model.entity;

import com.foodordering.model.enums.DiscountType;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // 優惠碼 (例如: SUMMER88)

    private String description; // 優惠說明

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType; // 折扣類型

    @Column(nullable = false)
    private BigDecimal discountValue; // 折扣數值 (若是 PERCENTAGE 放 0.8 代表 8 折；若是 FIXED 放 50 代表折 50 元)

    @Column(nullable = false)
    private BigDecimal minOrderAmount = BigDecimal.ZERO; // 最低消費門檻 (預設 0 元)

    private Boolean active = true; // 是否啟用中

    // 未來可以加上關聯欄位，用來指定「只有點特定餐點(Menu)」才能使用此優惠券
}