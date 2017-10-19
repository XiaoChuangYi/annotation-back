package cn.malgo.annotation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author 张钟
 * @date 2017/8/2
 */
@EnableAsync
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@MapperScan(basePackages = "cn.malgo.annotation.common.dal.mapper")
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}