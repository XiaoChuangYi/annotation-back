package cn.malgo.annotation.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.malgo.annotation.common.dal.model.CrmAccount;
import cn.malgo.annotation.common.util.AssertUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import cn.malgo.annotation.web.result.ResultVO;

/**
 *
 * @author ZhangZhong
 * @date 2017/5/6
 */
@ControllerAdvice
public class LoginInterceptor implements HandlerInterceptor {

    private Logger logger = Logger.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        HttpSession httpSession = request.getSession();
        CrmAccount crmAccount = (CrmAccount) httpSession.getAttribute("currentAccount");
        if (crmAccount==null) {
            //2017/11/20 ,session失效，isSucc设置为false
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
