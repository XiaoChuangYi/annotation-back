package cn.malgo.annotation.web.interceptor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by ZhangZhong on 2017/5/6.
 */
@Configuration
public class LoginInterceptorConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        List<String> excludePathPatterns = new ArrayList<>();
        excludePathPatterns.add("/*/anonymous/*.do");


        InterceptorRegistration interceptorRegistration = registry
            .addInterceptor(new LoginInterceptor()).addPathPatterns("/**");
        for (String excludePath : excludePathPatterns) {
            interceptorRegistration.excludePathPatterns(excludePath);
        }
        super.addInterceptors(registry);
    }
}
