package cn.malgo.annotation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author 张钟
 * @date 2017/8/2
 */
@EnableTransactionManagement
@EnableAsync//告诉springBoot开启异步调用功能
@SpringBootApplication
@EnableFeignClients//开启Feign功能(伪客户端功能，便捷访问其它服务器)
@EnableScheduling
@MapperScan(basePackages = "cn.malgo.annotation.common.dal.mapper")
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

}