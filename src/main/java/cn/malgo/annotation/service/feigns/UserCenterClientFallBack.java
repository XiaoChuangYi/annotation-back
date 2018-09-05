package cn.malgo.annotation.service.feigns;

import cn.malgo.annotation.vo.UserInfo;
import feign.hystrix.FallbackFactory;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserCenterClientFallBack implements FallbackFactory<UserCenterClient> {

  @Override
  public UserCenterClient create(Throwable cause) {
    return () -> {
      log.error("调用用户中心列表接口：{}；失败原因：{};", "/api/user/list-users", cause.getMessage());
      return new UserInfo();
    };
  }
}
