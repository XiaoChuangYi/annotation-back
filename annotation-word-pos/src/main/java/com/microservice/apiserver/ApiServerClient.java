package com.microservice.apiserver;

import com.microservice.apiserver.request.UpdateAnnotationRequest;
import com.microservice.apiserver.result.AnnotationResult;
import com.microservice.vo.TermVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by cjl on 2018/4/11.
 */
@FeignClient(name = "apiServerClient",url = "${pro.serverApiUrl}",fallbackFactory = ApiServerClientFallBack.class)
public interface ApiServerClient {
    /**
     * demo接口
     * @param text
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/docPosTag")
    String docPosTag(@RequestParam("text") String text);

    /**
     * 自动标注
     * @param text
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/phraseTokenize")
    String phraseTokenize(@RequestParam("text") String text);

    /**
     * 批量自动标注
     * @param texts
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/batchPhraseTokenize")
    List<AnnotationResult> batchPhraseTokenize(@RequestBody List<TermVO> texts);

    /**
     * 自动标注,通过给定的新词和手工标注
     * @param updateAnnotationRequest
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/phraseUpdatePosWithNewTerm")
    String phraseUpdatePosWithNewTerm(UpdateAnnotationRequest updateAnnotationRequest);

    /**
     * 批量请求附带新词的和手工标注的最终标注
     * @param updateAnnotationDTOList
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/batchPhraseUpdatePosWithNewTerm")
    List<AnnotationResult> batchPhraseUpdatePosWithNewTerm(List<UpdateAnnotationRequest> updateAnnotationDTOList);

}
