package com.malgo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
public class AnnotationCombineApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnnotationCombineApplication.class, args);
	}

//	@Bean
//	public AddAnnotationAlgorithmBiz<WorSerIm> wordAddAlgorithmBiz() {
//		return new Al
//	}
}
