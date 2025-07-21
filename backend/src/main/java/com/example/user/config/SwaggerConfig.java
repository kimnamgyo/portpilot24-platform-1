package com.example.user.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "API 문서", version = "v1", description = "Swagger API 명세서")
)
@Configuration
public class SwaggerConfig {
    // 별도 bean 등록 필요 없음 (Springdoc 2.x 기준)
}
