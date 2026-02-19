package com.foodordering.repository;

import com.foodordering.model.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    // 幫我們找所有「上架中 (available = true)」的餐點
    List<Menu> findByAvailableTrue();
}