package com.microservice.apiserver;

import com.alibaba.fastjson.JSON;
import com.microservice.apiserver.request.AnnotationOriginTextRequest;
import com.microservice.apiserver.request.UpdateAnnotationRequest;
import com.microservice.apiserver.result.AnnotationResult;
import com.microservice.apiserver.vo.TermTypeVO;
import com.microservice.dataAccessLayer.entity.Annotation;
import com.microservice.dataAccessLayer.entity.Corpus;
import com.microservice.vo.TermVO;
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
        System.out.println(">>>>>>>>>>>>>>>>>>>>过apiServer参数："+ JSON.toJSONString(updateAnnotationRequestList));
        List<AnnotationResult> annotationResults = apiServerClient
                .batchUpdateAnnotationTokenizePos(updateAnnotationRequestList);

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
    public List<AnnotationResult> batchPhraseTokenize(List<Corpus> termList){

        List<TermVO> termVOList = convertToTermVOList(termList);

        List<AnnotationResult> result = apiServerClient.batchPhraseTokenize(termVOList);

        return result;

    }


    /**
     * 由annotation表的原始文本通过ApiServer批量获取预标注
     * 批量火气病历的预标注
     */

    public List<Annotation>  batchTokenizePos(List<Annotation> originAnnotationList){
        if(originAnnotationList.isEmpty())
            return  originAnnotationList;

        List<AnnotationOriginTextRequest> annotationOriginTextRequestList=new ArrayList<>();
        for (Annotation  annotation:originAnnotationList){
//            UpdateAnnotationRequest updateAnnotationRequest = new UpdateAnnotationRequest();
//            updateAnnotationRequest.setText(annotation.getTerm());
//            updateAnnotationRequest.setId(annotation.getId());
//            updateAnnotationRequest.setAutoAnnotation(annotation.getAutoAnnotation()==null?"":annotation.getAutoAnnotation());
//            List<TermTypeVO> termTypeVOList = TermTypeVO.convertFromString(annotation.getNewTerms());
//            updateAnnotationRequest.setNewTerms(termTypeVOList);
//            updateAnnotationRequest.setManualAnnotation(annotation.getManualAnnotation());
            AnnotationOriginTextRequest annotationOriginTextRequest=new AnnotationOriginTextRequest();
            annotationOriginTextRequest.setId(annotation.getId());
            annotationOriginTextRequest.setText(annotation.getTerm());
            annotationOriginTextRequestList.add(annotationOriginTextRequest);
        }
        List<AnnotationResult> annotationResultList=apiServerClient.listCasePrepareAnnotation(annotationOriginTextRequestList);

        if(annotationResultList!=null&&annotationResultList.size()>0){
            //如果标注结果不为空,重新组装最终标注
            Map<String, String> finalAnnotationMap = new HashMap<>();
            for(AnnotationResult annotationResult: annotationResultList){
                if(AnnotationResult.check(annotationResult)){
                    finalAnnotationMap.put(annotationResult.getId(),annotationResult.getAnnotation());
                }
            }
            for(Annotation annotation : originAnnotationList){
                String finalAnnotation = finalAnnotationMap.get(annotation.getId());
                if(StringUtils.isNotBlank(finalAnnotation)){
                    annotation.setFinalAnnotation(finalAnnotation);
                }
            }
        }
        return originAnnotationList;
    }

//    /**
//     * 根据原始文本通过ApiServer批量获取预标注
//     *
//     **/
//    public List<AnnotationResult>  batchTokenizePos(List<Corpus> corpusTermList){
//        List<AnnotationOriginTextRequest> annotationOriginTextRequestList=new ArrayList<>();
//        for (Corpus corpus:corpusTermList){
//            AnnotationOriginTextRequest annotationOriginTextRequest=new AnnotationOriginTextRequest();
//            annotationOriginTextRequest.setId(corpus.getId());
//            annotationOriginTextRequest.setText(corpus.getTerm());
//            annotationOriginTextRequestList.add(annotationOriginTextRequest);
//        }
//        List<AnnotationResult> annotationResultList=apiServerClient.batchTokenizePos(annotationOriginTextRequestList);
//        return annotationResultList;
//    }

    /**
     * 术语模型转换
     * @param corpusList
     * @return
     */
    private List<TermVO> convertToTermVOList(List<Corpus> corpusList){

        List<TermVO> termVOList = new ArrayList<>();
        for(Corpus corpus : corpusList){
            TermVO termVO = new TermVO();
            termVO.setText(corpus.getTerm());
            termVO.setId(corpus.getId());
            termVOList.add(termVO);
        }
        return termVOList;
    }
}
