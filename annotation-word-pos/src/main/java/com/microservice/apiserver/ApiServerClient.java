package com.microservice.apiserver;

import com.microservice.apiserver.request.AnnotationOriginTextRequest;
import com.microservice.apiserver.request.UpdateAnnotationRequest;
import com.microservice.apiserver.result.AnnotationResult;
import com.microservice.vo.TermVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by cjl on 2018/4/11.
 */
@FeignClient(name = "apiServerClient",url = "${pro.serverApiUrl}",fallbackFactory = ApiServerClientFallBack.class)
@Component(value = "apiServerClient")
public interface ApiServerClient {
    /**
     * demo接口
     * @param text
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/tokenize-pos-brat-ajax")
    String docPosTag(@RequestParam("text") String text);

    /**
     * 根据原始newTerms文本，获取对应的自动标注
     * @param text
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/rule-rule-tokenize")
    String phraseTokenize(@RequestParam("text") String text);


    /**
     * 根据单个原始文本，获取算法后台对应的标注
     */
    @RequestMapping(method = RequestMethod.GET ,value = "api/rule-tokenize-pos")
    String ruleTokenizePos(@RequestParam("text") String text);

    /**
     * 批量根据原始文本，获取算法后台的预标注
     *
     **/
    @RequestMapping(method = RequestMethod.POST,value = "/api/batch-tokenize-pos")
    List<AnnotationResult> batchTokenizePos(@RequestBody List<AnnotationOriginTextRequest> annotationOriginTextRequestList);

    /**
     * 批量自动标注
     * @param texts
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/batch-rule-tokenize")
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



    /**
     * 以下部分为新分词细化接口
     */

    /**
     * 病历分词
     */
    @RequestMapping(method=RequestMethod.GET,value = "/api/mr-tokenize")
    String getCaseWordPos(@RequestParam("text") String text);

    /**
     * 批量病历分词
     */
    @RequestMapping(method = RequestMethod.POST,value = "/api/batch-mr-tokenize")
    List<AnnotationResult> listCaseWordPos(@RequestBody List<AnnotationOriginTextRequest> annotationOriginTextRequestList);

    /**
     * 病历预标注
     */
    @RequestMapping(method = RequestMethod.GET,value = "/api/mr-tokenize-pos")
    String getCasePrepareAnnotation(@RequestParam("text") String text);

    /**
     * 批量病历预标注
     */
    @RequestMapping(method = RequestMethod.POST,value = "/api/batch-mr-tokenize-pos")
    List<AnnotationResult> listCasePrepareAnnotation(@RequestBody List<AnnotationOriginTextRequest> annotationOriginTextRequestList);

    /**
     * 药品分词
     */
    @RequestMapping(method = RequestMethod.GET,value = "/api/drug-tokenize")
    String getDrugWordPos(@RequestParam("text") String text);

    /**
     * 批量药品分词
     */
    @RequestMapping(method = RequestMethod.POST,value = "/api/batch-drug-tokenize")
    List<AnnotationResult> listDrugWordPos(@RequestBody List<AnnotationOriginTextRequest> annotationOriginTextRequestList);

    /**
     * 器材分词
     */
    @RequestMapping(method = RequestMethod.GET,value = "/api/device-tokenize")
    String getDeviceTokenize(@RequestParam("text") String text);

    /**
     * 批量器材分词
     */
    @RequestMapping(method = RequestMethod.POST,value = "/api/batch-device-tokenize")
    List<AnnotationResult> listDeviceTokenize(@RequestBody List<AnnotationOriginTextRequest> annotationOriginTextRequestList);

    /**
     * 更新分词和词性标注
     */
    @RequestMapping(method = RequestMethod.POST,value = "/api/update-tokenize-pos")
    String updateAnnotationTokenizePos(@RequestBody UpdateAnnotationRequest updateAnnotationRequest);

    /**
     * 批量更新分词和词性标注
     */
    @RequestMapping(method = RequestMethod.POST,value = "/api/batch-update-tokenize-pos")
    List<AnnotationResult> batchUpdateAnnotationTokenizePos(@RequestBody List<UpdateAnnotationRequest> updateAnnotationRequestList);

}
