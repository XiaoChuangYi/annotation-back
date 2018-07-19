package cn.malgo.annotation.config;

import cn.malgo.annotation.dao.UserAccountRepository;
import cn.malgo.annotation.interceptor.DefaultUserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Profile("dev")
@Configuration
@Order(HIGHEST_PRECEDENCE + 10)
@Slf4j
public class DevDefaultUserConfig implements WebMvcConfigurer {
  private final long defaultUserId;
  private final UserAccountRepository userAccountRepository;

  public DevDefaultUserConfig(
      @Value("${malgo.dev.default-user}") final long defaultUserId,
      final UserAccountRepository userAccountRepository) {
    this.defaultUserId = defaultUserId;
    this.userAccountRepository = userAccountRepository;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(new DefaultUserInterceptor(this.defaultUserId, this.userAccountRepository))
        .addPathPatterns("/**");
  }
}
