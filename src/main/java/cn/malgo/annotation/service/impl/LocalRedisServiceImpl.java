package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.service.LocalRedisService;
import org.springframework.stereotype.Service;

@Service
public class LocalRedisServiceImpl implements LocalRedisService {

  private static final ThreadLocal<String> local = new ThreadLocal<>();

  @Override
  public void setTicket(String ticket) {
    local.set(ticket);
  }

  @Override
  public String getTicket() {
    return local.get();
  }
}
