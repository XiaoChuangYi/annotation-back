package cn.malgo.annotation.service.feigns;

import cn.malgo.common.auth.AuthConstants;
import cn.malgo.common.auth.RedisConfigService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignInterceptor implements RequestInterceptor {

  private RedisConfigService redisConfigService;

  public FeignInterceptor(final RedisConfigService redisConfigService) {
    this.redisConfigService = redisConfigService;
  }

  @Override
  public void apply(RequestTemplate template) {
    final String ticket = redisConfigService.getStr(AuthConstants.ALL_SYSTEM_TICKET);
    template.header("ticket", ticket);
  }
}
