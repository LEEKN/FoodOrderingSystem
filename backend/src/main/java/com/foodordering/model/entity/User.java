package com.foodordering.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data // Lombok: 自動生成 Getter, Setter, toString
@Entity // JPA: 告訴程式這是一個對應資料庫的物件
@Table(name = "users") // 資料庫裡的表名叫做 users
public class User {

    @Id // 主鍵 (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動跳號 (1, 2, 3...)
    private Long id;

    @Column(nullable = false, unique = true) // 不能為空，且帳號不能重複
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    private String phone;

    private String address;

    private String role;
}