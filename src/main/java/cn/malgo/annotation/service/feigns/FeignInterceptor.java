package cn.malgo.annotation.service.feigns;

import cn.malgo.annotation.service.LocalRedisService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignInterceptor implements RequestInterceptor {

  private LocalRedisService localRedisService;

  public FeignInterceptor(final LocalRedisService localRedisService) {
    this.localRedisService = localRedisService;
  }

  @Override
  public void apply(RequestTemplate template) {
    final String ticket = localRedisService.getTicket();
    template.header("ticket", ticket);
  }
}
