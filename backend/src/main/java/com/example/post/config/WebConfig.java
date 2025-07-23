package com.example.post.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private String resourcePath = "/upload/**"; //프론트에서 이쪽으로 접근해줬으면 좋겠음....
    private String savePath = "file:///C:/springboot_img/"; // 실제 파일 저장 경로

    // upload 경로로 접근시 파일이 저장된 폴더 경로로 이어주는 역할
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(resourcePath)
                .addResourceLocations(savePath);
    }


}