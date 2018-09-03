package cn.malgo.annotation.controller;

import cn.malgo.annotation.request.LogOutRequest;
import cn.malgo.annotation.request.LoginRequest;
import cn.malgo.annotation.service.UserAccountService;
import cn.malgo.service.model.Response;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AuthController {

  private final UserAccountService userAccountService;

  public AuthController(final UserAccountService userAccountService) {
    this.userAccountService = userAccountService;
  }

  /** 用户登录 */
  @RequestMapping(value = "/user/login", method = RequestMethod.POST)
  public Response login(
      @RequestBody LoginRequest loginRequest,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse) {
    return new Response<>(userAccountService.login(loginRequest, servletRequest, servletResponse));
  }

  /** 用户登出 */
  @RequestMapping(value = "/log-out", method = RequestMethod.POST)
  public Response logout(
      @RequestBody LogOutRequest logOutRequest,
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse) {
    return new Response<>(
        userAccountService.logOut(logOutRequest, servletRequest, servletResponse));
  }

  @RequestMapping(value = "/log-refresh", method = RequestMethod.GET)
  public Response logRefresh(
      HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    return new Response<>(userAccountService.loginRefresh(servletRequest, servletResponse));
  }
}
