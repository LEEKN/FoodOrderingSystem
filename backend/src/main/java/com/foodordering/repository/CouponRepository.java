package com.foodordering.repository;

import com.foodordering.model.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    // 透過優惠碼來找優惠券
    Optional<Coupon> findByCode(String code);
}