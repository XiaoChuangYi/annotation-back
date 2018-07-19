package cn.malgo.annotation.service.feigns;

import cn.malgo.annotation.dto.AutoAnnotation;
import cn.malgo.annotation.dto.AutoAnnotationRequest;
import cn.malgo.annotation.dto.UpdateAnnotationAlgorithmRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(
    name = "algorithmApiClient",
    url = "${pro.serverApiUrl}",
    fallbackFactory = AlgorithmApiClientFallBack.class)
@Component(value = "algorithmApiClient")
public interface AlgorithmApiClient {
  /** 批量病历预标注 */
  @RequestMapping(method = RequestMethod.POST, value = "/api/batch-mr-tokenize-pos")
  List<AutoAnnotation> listCasePrepareAnnotation(
      @RequestBody List<AutoAnnotationRequest> annotationOriginTextRequestList);

  /** 批量更新分词和词性标注 */
  @RequestMapping(method = RequestMethod.POST, value = "/api/batch-update-tokenize-pos")
  List<AutoAnnotation> batchUpdateAnnotationTokenizePos(
      @RequestBody List<UpdateAnnotationAlgorithmRequest> updateAnnotationRequestList);

  /** 批量切分关联数据为短句API */
  @RequestMapping(method = RequestMethod.POST, value = "/api/batch-block-splitter")
  List<List<String>> batchBlockSplitter(
      @RequestBody List<AutoAnnotationRequest> updateAnnotationRequestList);
}
