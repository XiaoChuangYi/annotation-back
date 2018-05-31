package com.malgo.service.feigns;

import com.alibaba.fastjson.JSON;
import com.malgo.dto.AutoAnnotation;
import feign.hystrix.FallbackFactory;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by cjl on 2018/5/31.
 */
@Component
@Slf4j
public class AlgorithmApiClientFallBack implements FallbackFactory<AlgorithmApiClient> {

  @Override
  public AlgorithmApiClient create(Throwable throwable) {
    return new AlgorithmApiClient() {
      @Override
      public List<AutoAnnotation> listCasePrepareAnnotation(List<Object> annotationOriginTextRequestList) {
        log.error("调用算法后台预标注接口：{}请求数据为：{}；失败原因：{};", "/api/batch-mr-tokenize-pos",JSON.toJSONString(annotationOriginTextRequestList),throwable.getMessage());
        return null;
      }

      @Override
      public List<Object> batchUpdateAnnotationTokenizePos(
          List<Object> updateAnnotationRequestList) {
        log.error("调用算法后台预标注接口：{}请求数据为：{}；失败原因：{};", "/api/batch-update-tokenize-pos",JSON.toJSONString(updateAnnotationRequestList),throwable.getMessage());
        return null;
      }
    };
  }
}
