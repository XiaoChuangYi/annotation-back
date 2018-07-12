package cn.malgo.annotation.service.feigns;

import cn.malgo.annotation.dto.AutoAnnotation;
import cn.malgo.annotation.dto.AutoAnnotationRequest;
import cn.malgo.annotation.dto.UpdateAnnotationAlgorithm;
import com.alibaba.fastjson.JSON;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AlgorithmApiClientFallBack implements FallbackFactory<AlgorithmApiClient> {
  @Override
  public AlgorithmApiClient create(Throwable throwable) {
    return new AlgorithmApiClient() {
      @Override
      public List<AutoAnnotation> listCasePrepareAnnotation(
          List<AutoAnnotationRequest> annotationOriginTextRequestList) {
        log.error(
            "调用算法后台预标注接口：{}请求数据为：{}；失败原因：{};",
            "/api/batch-mr-tokenize-pos",
            JSON.toJSONString(annotationOriginTextRequestList),
            throwable.getMessage());
        return null;
      }

      @Override
      public List<AutoAnnotation> batchUpdateAnnotationTokenizePos(
          List<UpdateAnnotationAlgorithm> updateAnnotationRequestList) {
        log.error(
            "调用算法后台预标注接口：{}请求数据为：{}；失败原因：{};",
            "/api/batch-update-tokenize-pos",
            JSON.toJSONString(updateAnnotationRequestList),
            throwable.getMessage());
        return null;
      }

      @Override
      public List<List<String>> batchBlockSplitter(
          final List<AutoAnnotationRequest> updateAnnotationRequestList) {
        log.error("调用算法后台切分关联数据接口失败，request: " + updateAnnotationRequestList, throwable);

        return updateAnnotationRequestList
            .stream()
            .map(request -> Collections.singletonList(request.getText()))
            .collect(Collectors.toList());
      }
    };
  }
}
