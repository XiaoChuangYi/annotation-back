package com.malgo.interceptor;

import com.alibaba.fastjson.JSON;
import com.malgo.entity.UserAccount;
import com.malgo.result.Response;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by cjl on 2018/5/30.
 */
@ControllerAdvice
public class LoginInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) throws Exception {
    response.setHeader("Content-type", "text/html;charset=UTF-8");
    HttpSession httpSession = request.getSession();
    UserAccount account = (UserAccount) httpSession.getAttribute("userAccount");
    if (account == null) {
      Response failResult = new Response(null, "请重新登录!");
      response.getWriter().write(JSON.toJSONString(failResult));
      return false;
    }

    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
  }


}
