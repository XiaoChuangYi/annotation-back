package cn.malgo.annotation.common.service.integration.apiserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
import cn.malgo.annotation.common.service.integration.apiserver.request.UpdateAnnotationRequest;
import cn.malgo.annotation.common.service.integration.apiserver.result.AnnotationResult;
import cn.malgo.annotation.common.service.integration.apiserver.vo.TermTypeVO;

/**
 * Created by 张钟 on 2017/10/18.
 */
@Service
public class ApiServerService {

    @Autowired
    private ApiServerClient apiServerClient;

    /**
     * demo接口
     * @param text
     * @return
     */
    public String docPosTag(String text) {
        return apiServerClient.docPosTag(text);
    }

    /**
     * 自动标注
     * @param text
     * @return
     */
    public String phraseTokenize(String text) {
        String result = apiServerClient.phraseTokenize(text);
        //TODO 后续处理
        return result;
    }

    /**
     * 自动标注,通过给定的新词和手工标注
     * @param text
     * @param newTerms
     * @param autoAnnotation
     * @param manualAnnotation
     * @return
     */
    public String phraseUpdatePosWithNewTerm(String text, String newTerms, String autoAnnotation,
                                             String manualAnnotation) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", text);
        jsonObject.put("new_terms", JSONArray.parseArray(newTerms));
        jsonObject.put("Token", autoAnnotation);
        jsonObject.put("Manual", manualAnnotation);
        String result = apiServerClient.phraseUpdatePosWithNewTerm(jsonObject);
        if (result != null) {
            //TODO 后续处理
        }
        return result;
    }

    /**
     * 批量标注,使用手工标注和新词
     * @param anTermAnnotationList
     * @param manualAnnotation
     * @param newTerms
     * @return
     */
    public List<AnTermAnnotation> batchPhraseUpdatePosWithNewTerm(List<AnTermAnnotation> anTermAnnotationList,
                                                           String manualAnnotation,
                                                           List<TermTypeVO> newTerms) {
        Map<String, String> finalAnnotationMap = new HashMap<>();
        List<UpdateAnnotationRequest> updateAnnotationRequestList = new ArrayList<>();

        for (AnTermAnnotation anTermAnnotation : anTermAnnotationList) {
            UpdateAnnotationRequest updateAnnotationRequest = new UpdateAnnotationRequest();
            updateAnnotationRequest.setText(anTermAnnotation.getTerm());
            updateAnnotationRequest.setId(anTermAnnotation.getId());
            updateAnnotationRequest.setAutoAnnotation(anTermAnnotation.getAutoAnnotation());
            updateAnnotationRequest.setNewTerms(newTerms);
            updateAnnotationRequest.setManualAnnotation(manualAnnotation);
            updateAnnotationRequestList.add(updateAnnotationRequest);
        }

        //使用手工标注和新词批量标注
        List<AnnotationResult> annotationResults = apiServerClient
            .batchPhraseUpdatePosWithNewTerm(updateAnnotationRequestList);

        //如果标注结果不为空,重新组装最终标注
        if(annotationResults!=null){
            for(AnnotationResult annotationResult: annotationResults){
                if(AnnotationResult.chekc(annotationResult)){
                    finalAnnotationMap.put(annotationResult.getId(),annotationResult.getAnnotation());
                }
            }

            String newTermString = TermTypeVO.convertToString(newTerms);
            for(AnTermAnnotation anTermAnnotation:anTermAnnotationList){
                //使用手工标注和新词重新标注后的结果存在
                String finalAnnotation = finalAnnotationMap.get(anTermAnnotation.getId());
                if(StringUtils.isNotBlank(finalAnnotation)){
                    anTermAnnotation.setFinalAnnotation(finalAnnotation);
                }
                anTermAnnotation.setNewTerms(newTermString);
                anTermAnnotation.setManualAnnotation(manualAnnotation);
            }
        }

        return anTermAnnotationList;
    }
}
