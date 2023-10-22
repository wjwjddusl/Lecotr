package com.mysite.bookstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private String resourcePath = "/upload/**"; //view 에서 접근할 경로
    private String savePath ="file:///D:/JAVA/bookstore_img/"; //실제 파일저장 경로
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler(resourcePath).addResourceLocations(savePath);
        //view 에서 resourcePath 로 접근시 savePath 에서 찾아줌
    }
}
