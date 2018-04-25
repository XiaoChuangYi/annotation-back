package com.microservice.interceptor;

import com.alibaba.fastjson.JSON;
import com.microservice.dataAccessLayer.entity.UserAccount;
import com.microservice.result.ResultVO;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by cjl on 2018/4/16.
 */
@Component
public class RewriteFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        String servletPath=request.getServletPath();
        String context=request.getContextPath();
//        System.out.println(">>>>>>>>>>>>>>>>>>:"+servletPath);

        if(servletPath.contains("ui")){
            servletRequest.getRequestDispatcher(context+"/index.html").forward(servletRequest,servletResponse);
        }else{
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
