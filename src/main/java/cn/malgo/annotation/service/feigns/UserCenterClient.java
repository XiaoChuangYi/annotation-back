package cn.malgo.annotation.service.feigns;

import cn.malgo.annotation.vo.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(
    url = "${cn.malgo.auth.user.url}",
    name = "userCenterClient",
    fallbackFactory = UserCenterClientFallBack.class)
@Component(value = "userCenterClient")
public interface UserCenterClient {

  @RequestMapping(method = RequestMethod.POST, value = "/api/user/get-users")
  UserInfo getUsers();
}
