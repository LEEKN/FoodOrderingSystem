# 點餐系統 後端 API (Food Ordering System - Backend)

## 📝 專案目的
本專案為一個前後端分離的點餐系統後端 API，旨在提供完整的會員註冊登入、菜單瀏覽與管理、以及購物車點餐功能。這是我作為 Java 後端工程師求職的作品集。

## 🛠️ 技術選型與環境
* **核心框架**: Java 21, Spring Boot 3.2.2
* **資料庫 & ORM**: MySQL, Spring Data JPA
* **安全性**: Spring Security, JWT (JSON Web Token)
* **API 文件**: Swagger UI / SpringDoc
* **建置工具**: Gradle

## 📂 檔案功能與系統架構
專案採用標準的分層架構 (Controller -> Service -> Repository -> DB)：

* **設定檔與環境**
    * `build.gradle`: 定義專案依賴 (Spring Boot, JWT, MySQL, Swagger 等)。
    * `application.properties`: 資料庫連線、JPA 參數設定。
* **安全與認證層 (Security)**
    * `SecurityConfig`: 設定 API 路由的訪問權限 (例如公開看菜單，登入才能點餐)。
    * `JwtAuthenticationFilter`: 攔截請求，驗證 JWT Token 的有效性。
    * `JwtUtil`: 負責生成與解析 JWT Token。
    * `CustomUserDetailsService`: 橋接資料庫與 Spring Security 的使用者驗證。
* **會員模組 (Auth/User)**
    * `AuthController` & `AuthService`: 處理註冊 (`/register`) 與登入 (`/login`) 邏輯。
    * `User`, `UserRepository`: 會員實體與資料庫操作。
* **菜單模組 (Menu)**
    * `MenuController` & `MenuService`: 處理菜單的查詢 (公開) 與新增 (需權限)。
    * `Menu`, `MenuRepository`: 餐點實體與資料庫操作。
* **訂單模組 (Order)**
    * `Order`, `OrderItem`, `OrderStatus`: 訂單主檔、明細與狀態列舉。
    * `OrderRequest`, `OrderItemRequest`: 接收前端點餐請求的 DTO。
    * `OrderService`: 處理點餐邏輯、計算總價與庫存檢查 (開發中)。

## 🚀 目前進度 (Current Status)
- [x] 專案基礎環境建置與資料庫連線。
- [x] 會員註冊、登入與 JWT 核發驗證系統。
- [x] 菜單 (Menu) 模組 CRUD 完成，支援遊客瀏覽與管理員新增。
- [x] 訂單 (Order) 模組實體 (Entity)、資料傳輸物件 (DTO) 與商業邏輯 (Service) 建置完成。

## 🎯 未來目標 (Next Steps)
1. 完成 `OrderController` 並進行 API 點餐測試。
2. 開發「優惠券 (Coupon)」核心邏輯與折扣計算。
3. 實作更嚴謹的 RBAC 權限控管 (嚴格區分 `ROLE_USER` 與 `ROLE_ADMIN` 的可執行動作)。

---
*最後更新時間：2026-02-19*