package com.microservice.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Created by cjl on 2018/4/16.
 */
@Configuration
public class LoginAdapter extends WebMvcConfigurationSupport {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");

  }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**")
          .excludePathPatterns("/*/anonymous/*.do");
        super.addInterceptors(registry);
    }
}
