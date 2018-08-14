package cn.malgo.annotation.service.impl.error;

import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.service.AnnotationErrorFactory;
import cn.malgo.annotation.service.AnnotationErrorProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class AnnotationErrorFactoryImpl implements AnnotationErrorFactory {
  private final ApplicationContext applicationContext;
  private final Map<AnnotationErrorEnum, AnnotationErrorProvider> providerMap = new HashMap<>();

  public AnnotationErrorFactoryImpl(final ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @PostConstruct
  private void init() {
    final Map<String, AnnotationErrorProvider> providerMap =
        applicationContext.getBeansOfType(AnnotationErrorProvider.class);

    for (AnnotationErrorProvider provider : providerMap.values()) {
      this.providerMap.put(provider.getErrorEnum(), provider);
    }
  }

  @Override
  public AnnotationErrorProvider getProvider(final AnnotationErrorEnum errorEnum) {
    if (!providerMap.containsKey(errorEnum)) {
      throw new IllegalArgumentException("不存在的错误类型: " + errorEnum);
    }

    return providerMap.get(errorEnum);
  }
}
