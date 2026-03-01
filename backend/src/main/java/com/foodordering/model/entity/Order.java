package com.foodordering.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodordering.model.enums.OrderStatus;
import com.foodordering.model.entity.Coupon;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders") // "order" 是 SQL 保留字，所以表名最好加 s
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 關聯到 User (誰點的單)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // 訂單總金額
    @Column(nullable = false)
    private BigDecimal totalAmount;

    // 訂單狀態 (預設為 PENDING)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    // 下單時間 (自動產生)
    private LocalDateTime createdAt = LocalDateTime.now();

    // 關聯到 OrderItem (這張單包含哪些明細)
    // cascade = CascadeType.ALL 代表如果我刪除/儲存這張單，裡面的明細也會一起被處理
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 新增：紀錄這張訂單折了多少錢
    private BigDecimal discountAmount = BigDecimal.ZERO;

    // 新增：關聯到 Coupon (這張單用了哪張優惠券？可為空)
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
}