package com.foodordering.service;

import com.foodordering.model.entity.Menu;
import com.foodordering.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    // 1. 查詢所有餐點 (給店家後台看，包含下架的)
    public List<Menu> findAllMenus() {
        return menuRepository.findAll();
    }

    // 2. 查詢上架餐點 (給客人看)
    public List<Menu> findAvailableMenus() {
        return menuRepository.findByAvailableTrue();
    }

    // 3. 新增餐點
    public Menu createMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    // 4. 根據 ID 找餐點
    public Menu findMenuById(Long id) {
        return menuRepository.findById(id).orElse(null);
    }
}