package com.microservice.apiserver;

import com.microservice.apiserver.request.UpdateAnnotationRequest;
import com.microservice.apiserver.result.AnnotationResult;
import com.microservice.apiserver.vo.TermTypeVO;
import com.microservice.dataAccessLayer.entity.Annotation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cjl on 2018/4/11.
 */
@Service
public class ApiServerService {

    @Autowired
    private ApiServerClient apiServerClient;

    /**
     * 批量标注,使用手工标注和新词
     * @param annotationList
     * @return
     */
    public List<Annotation> batchPhraseUpdatePosWithNewTerm(List<Annotation> annotationList) {
        if(annotationList.isEmpty()){
            return annotationList;
        }
        Map<String, String> finalAnnotationMap = new HashMap<>();
        List<UpdateAnnotationRequest> updateAnnotationRequestList = new ArrayList<>();

        for (Annotation annotation : annotationList) {
            UpdateAnnotationRequest updateAnnotationRequest = new UpdateAnnotationRequest();
            updateAnnotationRequest.setText(annotation.getTerm());
            updateAnnotationRequest.setId(annotation.getId());
            updateAnnotationRequest.setAutoAnnotation(annotation.getAutoAnnotation());
            List<TermTypeVO> termTypeVOList = TermTypeVO.convertFromString(annotation.getNewTerms());
            updateAnnotationRequest.setNewTerms(termTypeVOList);
            updateAnnotationRequest.setManualAnnotation(annotation.getManualAnnotation());
            updateAnnotationRequestList.add(updateAnnotationRequest);
        }

        //使用手工标注和新词批量标注
        List<AnnotationResult> annotationResults = apiServerClient
                .batchPhraseUpdatePosWithNewTerm(updateAnnotationRequestList);

        //如果标注结果不为空,重新组装最终标注
        if(annotationResults!=null){
            for(AnnotationResult annotationResult: annotationResults){
                if(AnnotationResult.check(annotationResult)){
                    finalAnnotationMap.put(annotationResult.getId(),annotationResult.getAnnotation());
                }
            }

            for(Annotation annotation : annotationList){
                //使用手工标注和新词重新标注后的结果存在
                String finalAnnotation = finalAnnotationMap.get(annotation.getId());
                if(StringUtils.isNotBlank(finalAnnotation)){
                    annotation.setFinalAnnotation(finalAnnotation);
                }
            }
        }

        return annotationList;
    }

    /**
     * 批量获取自动标注
     * @param termList
     * @return
     */
//    public List<AnnotationResult> batchPhraseTokenize(List<Corpus> termList){
//
//        List<TermVO> termVOList = convertToTermVOList(termList);
//
//        List<AnnotationResult> result = apiServerClient.batchPhraseTokenize(termVOList);
//
//        return result;
//
//    }
}
