package cn.malgo.annotation.service.feigns;

import cn.malgo.annotation.dto.User;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(
    name = "userCenterClient",
    url = "${user.serverApiUrl}",
    fallbackFactory = AlgorithmApiClientFallBack.class)
@Component(value = "userCenterClient")
public interface UserCenterClient {

  @RequestMapping(method = RequestMethod.GET, value = "/api/user/get-users")
  List<User> getUsers();
}
