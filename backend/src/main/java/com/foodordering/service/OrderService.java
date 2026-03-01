package com.foodordering.service;

import com.foodordering.model.dto.OrderItemRequest;
import com.foodordering.model.dto.OrderRequest;
import com.foodordering.model.entity.Menu;
import com.foodordering.model.entity.Coupon;
import com.foodordering.model.entity.Order;
import com.foodordering.model.entity.OrderItem;
import com.foodordering.model.entity.User;
import com.foodordering.model.enums.DiscountType;
import com.foodordering.repository.CouponRepository;
import com.foodordering.repository.MenuRepository;
import com.foodordering.repository.OrderRepository;
import com.foodordering.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private MenuRepository menuRepository;
    @Autowired private CouponRepository couponRepository;

    @Transactional
    public Order createOrder(String username, OrderRequest request) {

        // 1. 確認是哪位客人點的餐
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        // 2. 建立訂單主檔
        Order order = new Order();
        order.setUser(user);
        BigDecimal totalAmount = BigDecimal.ZERO; // 初始化總金額

        // 3. 處理客人點的每一個品項
        for (OrderItemRequest itemReq : request.getItems()) {
            Menu menu = menuRepository.findById(itemReq.getMenuId())
                    .orElseThrow(() -> new RuntimeException("找不到餐點 ID: " + itemReq.getMenuId()));

            if (!menu.getAvailable()) {
                throw new RuntimeException("抱歉，餐點已下架: " + menu.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenu(menu);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(menu.getPrice());

            BigDecimal subTotal = menu.getPrice().multiply(new BigDecimal(itemReq.getQuantity()));
            totalAmount = totalAmount.add(subTotal);

            order.getOrderItems().add(orderItem);
        }

        // --- 4. 處理優惠券邏輯 ---
        BigDecimal finalTotal = totalAmount; // 最終金額先等於原價
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                    .orElseThrow(() -> new RuntimeException("無效的優惠碼"));

            if (!coupon.getActive()) {
                throw new RuntimeException("此優惠券已失效");
            }

            if (totalAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
                throw new RuntimeException("未達到優惠券最低消費金額: " + coupon.getMinOrderAmount());
            }

            if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                discountAmount = coupon.getDiscountValue();
            } else if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
                BigDecimal discountedPrice = totalAmount.multiply(coupon.getDiscountValue());
                discountAmount = totalAmount.subtract(discountedPrice);
            }

            if (discountAmount.compareTo(totalAmount) > 0) {
                discountAmount = totalAmount;
            }

            finalTotal = totalAmount.subtract(discountAmount);
            order.setCoupon(coupon);
        }

        // 5. 設定算好的總金額與折扣，並存入資料庫
        order.setTotalAmount(finalTotal);
        order.setDiscountAmount(discountAmount);

        return orderRepository.save(order);
    }

    // --- 查詢使用者的歷史訂單 ---
    public List<Order> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));
        return orderRepository.findByUserId(user.getId());
    }
}