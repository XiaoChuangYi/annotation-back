package cn.malgo.annotation.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@ComponentScan(basePackages = "com.alibaba.fastjson.support.spring")
public class JsonConfig implements WebMvcConfigurer {
  static {
    JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
  }

  @Value("${malgo.config.fastjson.pretty}")
  private boolean prettyOutput;

  @Override
  public void configureMessageConverters(@NotNull List<HttpMessageConverter<?>> converters) {
    final FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

    if (prettyOutput) {
      FastJsonConfig fastJsonConfig = new FastJsonConfig();
      fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
      fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
      fastConverter.setFastJsonConfig(fastJsonConfig);
    }

    converters.add(0, fastConverter);
  }
}
