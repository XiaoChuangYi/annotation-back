package cn.malgo.annotation.config;

import cn.malgo.annotation.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthConfig implements WebMvcConfigurer {
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(new LoginInterceptor())
        .addPathPatterns("/**")
        .excludePathPatterns("/api/v2/user/login")
        .excludePathPatterns("/api/v2/import")
        .excludePathPatterns("/api/v2/doc/import")
        .excludePathPatterns("/static/*");
  }
}
