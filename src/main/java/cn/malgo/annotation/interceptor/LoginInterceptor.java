package cn.malgo.annotation.interceptor;

import cn.malgo.annotation.dto.UserDetails;
import cn.malgo.annotation.result.Response;
import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;

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
    final UserDetails account = (UserDetails) httpSession.getAttribute("userAccount");
    if (account == null) {
      Response failResult = new Response(null, "请重新登录!");
      response.getWriter().write(JSON.toJSONString(failResult));
      return false;
    }

    return true;
  }
}
