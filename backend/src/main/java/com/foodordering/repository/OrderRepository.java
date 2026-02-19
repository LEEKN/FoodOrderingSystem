package com.foodordering.repository;

import com.foodordering.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // 這個方法未來可以讓客人查詢自己的歷史訂單
    List<Order> findByUserId(Long userId);
}