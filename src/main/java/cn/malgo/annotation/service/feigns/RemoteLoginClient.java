package cn.malgo.annotation.service.feigns;

import cn.malgo.annotation.request.RemoteLoginRequest;
import cn.malgo.service.model.Response;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "malgo-user-center")
@Component(value = "malgoUserCenter")
public interface RemoteLoginClient {

  @RequestMapping(value = "/api/user/login", method = RequestMethod.POST)
  Response remoteLogin(
      @RequestBody RemoteLoginRequest request,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse);
}
