package cn.malgo.annotation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * Created by 张钟 on 2017/8/2.
 */
@SpringBootApplication
@EnableFeignClients
@MapperScan(basePackages = "cn.malgo.annotation.common.dal.mapper")
public class ManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageApplication.class, args);
    }
}

