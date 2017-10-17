//package com.hanma.crm.manage.config;
//
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.ResourceUtils;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
///**
// * Created by 张钟 on 2017/8/2.
// */
//@EnableWebMvc
//@Configuration
//public class WebConfig extends WebMvcConfigurerAdapter {
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//
//        registry.addResourceHandler("/templates/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/templates/**");
//        registry.addResourceHandler("/static/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/static/**");
//        super.addResourceHandlers(registry);
//    }
//
//}