package com.malgo.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Created by cjl on 2018/5/30.
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {


  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");

  }
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**").excludePathPatterns("/api/v2/user/login");
    super.addInterceptors(registry);
  }
}
