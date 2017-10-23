package cn.malgo.annotation.common.service.integration.apiserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.malgo.annotation.common.dal.model.AnTerm;
import cn.malgo.annotation.common.service.integration.apiserver.vo.TermVO;
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
     * 批量标注,使用手工标注和新词
     * @param anTermAnnotationList
     * @return
     */
    public List<AnTermAnnotation> batchPhraseUpdatePosWithNewTerm(List<AnTermAnnotation> anTermAnnotationList) {
        if(anTermAnnotationList.isEmpty()){
            return anTermAnnotationList;
        }
        Map<String, String> finalAnnotationMap = new HashMap<>();
        List<UpdateAnnotationRequest> updateAnnotationRequestList = new ArrayList<>();

        for (AnTermAnnotation anTermAnnotation : anTermAnnotationList) {
            UpdateAnnotationRequest updateAnnotationRequest = new UpdateAnnotationRequest();
            updateAnnotationRequest.setText(anTermAnnotation.getTerm());
            updateAnnotationRequest.setId(anTermAnnotation.getId());
            updateAnnotationRequest.setAutoAnnotation(anTermAnnotation.getAutoAnnotation());
            List<TermTypeVO> termTypeVOList = TermTypeVO.convertFromString(anTermAnnotation.getNewTerms());
            updateAnnotationRequest.setNewTerms(termTypeVOList);
            updateAnnotationRequest.setManualAnnotation(anTermAnnotation.getManualAnnotation());
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

            for(AnTermAnnotation anTermAnnotation:anTermAnnotationList){
                //使用手工标注和新词重新标注后的结果存在
                String finalAnnotation = finalAnnotationMap.get(anTermAnnotation.getId());
                if(StringUtils.isNotBlank(finalAnnotation)){
                    anTermAnnotation.setFinalAnnotation(finalAnnotation);
                }
            }
        }

        return anTermAnnotationList;
    }

    /**
     * 批量获取自动标注
     * @param termList
     * @return
     */
    public List<AnnotationResult> batchPhraseTokenize(List<AnTerm> termList){

        List<TermVO> termVOList = convertToTermVOList(termList);

        List<AnnotationResult> result = apiServerClient.batchPhraseTokenize(termVOList);

        return result;

    }


    /**
     * 术语模型转换
     * @param anTermList
     * @return
     */
    private List<TermVO> convertToTermVOList(List<AnTerm> anTermList){

        List<TermVO> termVOList = new ArrayList<>();
        for(AnTerm anTerm : anTermList){
            TermVO termVO = new TermVO();
            termVO.setText(anTerm.getTerm());
            termVO.setId(anTerm.getId());
            termVOList.add(termVO);
        }
        return termVOList;
    }
}
