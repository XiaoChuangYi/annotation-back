package cn.malgo.annotation.service.feigns;

import cn.malgo.annotation.request.GetUsersRequest;
import cn.malgo.annotation.vo.UserInfo;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserCenterClientFallBack implements FallbackFactory<UserCenterClient> {

  @Override
  public UserCenterClient create(Throwable cause) {
    return (GetUsersRequest request) -> {
      log.error(
          "调用用户中心列表接口：{}；请求参数：{}；失败原因：{};", "/api/user/list-users", request, cause.getMessage());
      return new UserInfo();
    };
  }
}
