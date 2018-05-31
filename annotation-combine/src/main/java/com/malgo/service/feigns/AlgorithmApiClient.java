package com.malgo.service.feigns;

import com.malgo.dto.AutoAnnotation;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by cjl on 2018/5/31.
 */
@FeignClient(name = "algorithmApiClient",url = "${pro.serverApiUrl}",fallbackFactory = AlgorithmApiClientFallBack.class)
@Component(value = "algorithmApiClient")
public interface AlgorithmApiClient {

  /**
   * 批量病历预标注
   */
  @RequestMapping(method = RequestMethod.POST,value = "/api/batch-mr-tokenize-pos")
  List<AutoAnnotation> listCasePrepareAnnotation(@RequestBody List<Object> annotationOriginTextRequestList);


  /**
   * 批量更新分词和词性标注
   */
  @RequestMapping(method = RequestMethod.POST,value = "/api/batch-update-tokenize-pos")
  List<Object> batchUpdateAnnotationTokenizePos(@RequestBody List<Object> updateAnnotationRequestList);

}
