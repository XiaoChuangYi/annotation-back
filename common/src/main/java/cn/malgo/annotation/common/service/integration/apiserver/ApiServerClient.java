package cn.malgo.annotation.common.service.integration.apiserver;

import cn.malgo.annotation.common.service.integration.apiserver.request.UpdateAnnotationRequest;
import cn.malgo.annotation.common.service.integration.apiserver.result.AnnotationResult;
import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 *
 * @author 张钟
 * @date 2017/10/18
 */
@FeignClient(name = "apiServerClient", url = "${prc.server.api.url}", fallbackFactory = ApiServerClientFallBack.class)
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
     * 自动标注,通过给定的新词和手工标注
     * @param body
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/phraseUpdatePosWithNewTerm")
    String phraseUpdatePosWithNewTerm(JSONObject body);

    /**
     * 批量请求附带新词的和手工标注的最终标注
     * @param updateAnnotationDTOList
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/batchPhraseUpdatePosWithNewTerm")
    List<AnnotationResult>  batchPhraseUpdatePosWithNewTerm(List<UpdateAnnotationRequest> updateAnnotationDTOList);
}
