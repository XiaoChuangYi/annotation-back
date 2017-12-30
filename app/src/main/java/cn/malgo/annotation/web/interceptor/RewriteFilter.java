package cn.malgo.annotation.web.interceptor;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by cjl on 2017/12/20.
 */
@Component
public class RewriteFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException  {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        String servletPath=request.getServletPath();
        String context=request.getContextPath();
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
