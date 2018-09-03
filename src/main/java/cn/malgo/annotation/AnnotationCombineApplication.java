package cn.malgo.annotation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "cn.malgo")
@EnableFeignClients
@EnableJpaAuditing
@EnableScheduling
public class AnnotationCombineApplication {
  public static void main(String[] args) {
    SpringApplication.run(AnnotationCombineApplication.class, args);
  }
}
