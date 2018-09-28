package cn.malgo.annotation.config;

import cn.malgo.common.auth.UserService;
import cn.malgo.common.auth.interceptor.LoginInterceptor;
import cn.malgo.common.auth.interceptor.PermissionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthConfig implements WebMvcConfigurer {

  private UserService userService;

  public AuthConfig(final UserService userService) {
    this.userService = userService;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(new LoginInterceptor(userService))
        .addPathPatterns("/**")
        .excludePathPatterns("/api/v2/user/login")
        .excludePathPatterns("/api/v2/import")
        .excludePathPatterns("/api/v2/doc/import")
        .excludePathPatterns("/api/v2/list-type")
        .excludePathPatterns("/api/block/export-entities")
        .excludePathPatterns("/static/*");
    registry
        .addInterceptor(new PermissionInterceptor(userService))
        .addPathPatterns("/**")
        .excludePathPatterns("/api/v2/user/login")
        .excludePathPatterns("/api/v2/import")
        .excludePathPatterns("/api/v2/doc/import")
        .excludePathPatterns("/api/v2/list-type")
        .excludePathPatterns("/api/block/export-entities")
        .excludePathPatterns("/static/*");
  }
}
