package com.foodordering.controller;

import com.foodordering.model.entity.Menu;
import com.foodordering.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Autowired
    private MenuService menuService;

    // 1. 看菜單 (公開，所有人都能看)
    @GetMapping
    public List<Menu> getAllMenus() {
        return menuService.findAvailableMenus();
    }

    // 2. 新增菜單 (受保護，只有登入並帶有 Token 的人才能用)
    @PostMapping
    public Menu createMenu(@RequestBody Menu menu) {
        return menuService.createMenu(menu);
    }
}