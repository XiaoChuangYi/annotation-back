package cn.malgo.annotation.web.interceptor;

/**
 * Created by 张钟 on 2017/10/19.
 */

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 *  跨域过滤器
 * @author 张钟
 * @version
 * @since 2016年6月19日
 */
@Component
public class CrossDomainFilter implements Filter {

    @Value("${spring.profiles.active}")
    private String env;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {
//        if (!EnvironmentEnum.prod.name().equals(env)) {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers",
                "Content-Type, Content-Length, Authorization, Accept, X-Requested-With");
            chain.doFilter(req, res);
//        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
