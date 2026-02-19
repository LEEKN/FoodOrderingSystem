package com.foodordering;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.foodordering.model.entity")
// --- 新增下面這段設定 ---
@OpenAPIDefinition(
        info = @Info(title = "點餐系統 API", version = "1.0"),
        security = @SecurityRequirement(name = "bearerAuth") // 預設所有 API 都套用這個安全設定
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
// -----------------------
public class FoodOrderingApplication {
    public static void main(String[] args) {
        SpringApplication.run(FoodOrderingApplication.class, args);
    }
}