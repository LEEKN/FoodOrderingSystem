package com.foodordering.service;

import com.foodordering.model.dto.OrderItemRequest;
import com.foodordering.model.dto.OrderRequest;
import com.foodordering.model.entity.Menu;
import com.foodordering.model.entity.Order;
import com.foodordering.model.entity.OrderItem;
import com.foodordering.model.entity.User;
import com.foodordering.repository.MenuRepository;
import com.foodordering.repository.OrderRepository;
import com.foodordering.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private MenuRepository menuRepository;

    // @Transactional 非常重要！如果中途有任何錯誤（例如餐點找不到），
    // 整個資料庫操作都會「退回 (Rollback)」，不會產生寫入一半的髒資料。
    @Transactional
    public Order createOrder(String username, OrderRequest request) {

        // 1. 確認是哪位客人點的餐
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        // 2. 建立訂單主檔 (先暫存在記憶體中，等算完錢再存入資料庫)
        Order order = new Order();
        order.setUser(user);
        BigDecimal totalAmount = BigDecimal.ZERO; // 初始化總金額為 0

        // 3. 處理客人點的每一個品項
        for (OrderItemRequest itemReq : request.getItems()) {

            // 去菜單找這項餐點
            Menu menu = menuRepository.findById(itemReq.getMenuId())
                    .orElseThrow(() -> new RuntimeException("找不到餐點 ID: " + itemReq.getMenuId()));

            // 檢查是否下架
            if (!menu.getAvailable()) {
                throw new RuntimeException("抱歉，餐點已下架: " + menu.getName());
            }

            // 建立訂單明細
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order); // 綁定屬於哪張訂單
            orderItem.setMenu(menu);   // 綁定是哪個餐點
            orderItem.setQuantity(itemReq.getQuantity()); // 數量
            orderItem.setPrice(menu.getPrice()); // 紀錄「當下的結帳價格」

            // 計算這個品項的小計 (價格 * 數量)，並加到總金額中
            BigDecimal subTotal = menu.getPrice().multiply(new BigDecimal(itemReq.getQuantity()));
            totalAmount = totalAmount.add(subTotal);

            // 將明細加入訂單主檔中
            order.getOrderItems().add(orderItem);
        }

        // 4. 設定算好的總金額，並存入資料庫 (這裡會連同明細一起存進去)
        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }
}