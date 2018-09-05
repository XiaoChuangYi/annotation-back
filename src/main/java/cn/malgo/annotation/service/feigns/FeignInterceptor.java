package cn.malgo.annotation.service.feigns;

import cn.malgo.common.auth.RedisConfigService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeignInterceptor implements RequestInterceptor {

  private RedisConfigService redisConfigService;

  @Value("${malgo.annotation.redis.key}")
  private String systemRedisKey;

  public FeignInterceptor(final RedisConfigService redisConfigService) {
    this.redisConfigService = redisConfigService;
  }

  @Override
  public void apply(RequestTemplate template) {
    final String ticket = redisConfigService.getStr(systemRedisKey);
    template.header("ticket", ticket);
  }
}
