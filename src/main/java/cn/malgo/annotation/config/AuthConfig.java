package cn.malgo.annotation.config;

import cn.malgo.common.auth.RedisConfigService;
import cn.malgo.common.auth.interceptor.LoginInterceptor;
import cn.malgo.common.auth.interceptor.PermissionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthConfig implements WebMvcConfigurer {

  private RedisConfigService redisConfigService;

  public AuthConfig(final RedisConfigService redisConfigService) {
    this.redisConfigService = redisConfigService;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(new LoginInterceptor(redisConfigService))
        .addPathPatterns("/**")
        .excludePathPatterns("/api/v2/user/login")
        .excludePathPatterns("/api/v2/import")
        .excludePathPatterns("/api/v2/doc/import")
        .excludePathPatterns("/api/v2/list-type")
        .excludePathPatterns("/api/block/export-entities")
        .excludePathPatterns("/static/*");
    registry
        .addInterceptor(new PermissionInterceptor(redisConfigService))
        .addPathPatterns("/**")
        .excludePathPatterns("/api/v2/user/login")
        .excludePathPatterns("/api/v2/import")
        .excludePathPatterns("/api/v2/doc/import")
        .excludePathPatterns("/api/v2/list-type")
        .excludePathPatterns("/api/block/export-entities")
        .excludePathPatterns("/static/*");
  }
}
