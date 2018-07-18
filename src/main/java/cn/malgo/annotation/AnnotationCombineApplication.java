package cn.malgo.annotation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
@EnableCaching
@MapperScan("cn.malgo.annotation.mapper")
@EnableScheduling
public class AnnotationCombineApplication {
  public static void main(String[] args) {
    SpringApplication.run(AnnotationCombineApplication.class, args);
  }
}
