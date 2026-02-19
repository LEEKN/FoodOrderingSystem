package com.foodordering.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 屬於哪一張訂單
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 點了哪個餐點
    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    // 數量
    @Column(nullable = false)
    private Integer quantity;

    // 當時的單價 (重點：要記錄「下單當下」的價格，以免未來菜單漲價影響舊訂單)
    @Column(nullable = false)
    private BigDecimal price;
}