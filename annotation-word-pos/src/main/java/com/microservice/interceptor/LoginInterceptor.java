package com.microservice.interceptor;

import com.alibaba.fastjson.JSON;
import com.microservice.dataAccessLayer.entity.UserAccount;
import com.microservice.result.ResultVO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by cjl on 2018/4/16.
 */
@ControllerAdvice
public class LoginInterceptor implements HandlerInterceptor{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        HttpSession httpSession = request.getSession();
        UserAccount crmAccount = (UserAccount) httpSession.getAttribute("userAccount");
        if (crmAccount==null) {
            ResultVO failResult = new ResultVO(false, false, true, "请重新登录!");
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
