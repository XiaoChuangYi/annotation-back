package cn.malgo.annotation.interceptor;

import com.alibaba.fastjson.JSON;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.result.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@ControllerAdvice
public class LoginInterceptor implements HandlerInterceptor {
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    response.setHeader("Content-type", "application/json;charset=UTF-8");
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
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView)
      throws Exception {}

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {}
}
