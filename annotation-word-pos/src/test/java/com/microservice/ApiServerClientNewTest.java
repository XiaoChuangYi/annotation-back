package com.microservice;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.microservice.apiserver.ApiServerClient;
import com.microservice.apiserver.request.AnnotationOriginTextRequest;
import com.microservice.apiserver.request.UpdateAnnotationRequest;
import com.microservice.apiserver.result.AnnotationResult;
import com.microservice.apiserver.vo.TermTypeVO;
import com.microservice.vo.TermVO;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张钟 on 2017/10/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiServerClientNewTest {

    @Autowired
    private ApiServerClient apiServerClient;

    @Test
    @Ignore()
    public void testDocPosTag() {
        String result = apiServerClient.getCaseWordPos("右上肺腺癌");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>ApiServer<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(result);
    }

    @Test
    @Ignore
    public  void testRuleTokenizePos(){
        String annotationResult=apiServerClient.getCasePrepareAnnotation("胸腔镜下右上肺叶楔形切除术");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>ApiServer<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(annotationResult);
    }

    @Test
    @Ignore
    public void testBatchTokenizePos(){
        List<AnnotationOriginTextRequest> annotationOriginTextRequestList=new ArrayList<>();
        AnnotationOriginTextRequest annotationOriginTextRequest=new AnnotationOriginTextRequest();
        annotationOriginTextRequest.setId("1");
        annotationOriginTextRequest.setText("胸腔镜下右上肺叶楔形切除术");

        annotationOriginTextRequestList.add(annotationOriginTextRequest);
        annotationOriginTextRequest=new AnnotationOriginTextRequest();
        annotationOriginTextRequest.setId("2");
        annotationOriginTextRequest.setText("左肺叶切除术");
        annotationOriginTextRequestList.add(annotationOriginTextRequest);
        System.out.println(">>>>>>>>param："+JSON.toJSONString(annotationOriginTextRequestList));
        List<AnnotationResult> annotationResultList=apiServerClient.listCaseWordPos(annotationOriginTextRequestList);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>ApiServer<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(JSON.toJSONString(annotationResultList));
    }

    @Test
//    @Ignore()
    public void testPhraseTokenize() {
        String result = apiServerClient.phraseTokenize("胸部CT");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>ApiServer<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(result);
    }

    @Test
    @Ignore()
    public void test2() {
        List<TermVO> termVOList = new ArrayList<>();
        TermVO termVO = new TermVO();
        termVO.setId("123456");
        termVO.setText("胸部CT");
        termVOList.add(termVO);

        TermVO termVO2 = new TermVO();
        termVO2.setId("456789");
        termVO2.setText("舌前三分之二，腹面");
        termVOList.add(termVO2);

        List<AnnotationResult> results = apiServerClient.batchPhraseTokenize(termVOList);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>ApiServer<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(JSON.toJSONString(results));
    }



    @Test
    @Ignore()
    public void testBatchUpdateAnnotation() {
        List<UpdateAnnotationRequest> annotationRequestList = new ArrayList<>();
        UpdateAnnotationRequest updateAnnotationRequest = new UpdateAnnotationRequest();

        String newTermsStr = "[{\"term\":\"展神经\",\"type\":\"Body-structure\"},{\"term\":\"面\",\"type\":\"Zone\"},{\"term\":\"胰头\",\"type\":\"Body-structure\"}]";
        JSONArray jsonArray = JSONArray.parseArray(newTermsStr);
        List<TermTypeVO> termTypeVOList = jsonArray.toJavaList(TermTypeVO.class);

        updateAnnotationRequest.setNewTerms(termTypeVOList);
        updateAnnotationRequest.setAutoAnnotation(
            "T1\tToken 0 2\t舌前\nT2\tToken 2 6\t三分之二\nT3\tToken 6 7\t，\nT4\tToken 7 9\t腹面");
        updateAnnotationRequest.setId("1234567890");
        updateAnnotationRequest.setText("舌前三分之二，腹面");
        updateAnnotationRequest.setManualAnnotation("");

        annotationRequestList.add(updateAnnotationRequest);

        List<AnnotationResult> annotationResultList = apiServerClient
            .batchUpdateAnnotationTokenizePos(annotationRequestList);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>ApiServer<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(JSON.toJSONString(annotationResultList));
    }

    @Test
    @Ignore()
    public void testUpdateAnnotation() {
        UpdateAnnotationRequest updateAnnotationRequest = new UpdateAnnotationRequest();
        String newTermsStr = "[{\"term\":\"展神经\",\"type\":\"Body-structure\"},{\"term\":\"面\",\"type\":\"Zone\"},{\"term\":\"胰头\",\"type\":\"Body-structure\"}]";
        JSONArray jsonArray = JSONArray.parseArray(newTermsStr);
        List<TermTypeVO> termTypeVOList = jsonArray.toJavaList(TermTypeVO.class);

        updateAnnotationRequest.setNewTerms(termTypeVOList);
        updateAnnotationRequest.setAutoAnnotation(
                "T1\tToken 0 2\t舌前\nT2\tToken 2 6\t三分之二\nT3\tToken 6 7\t，\nT4\tToken 7 9\t腹面");
        updateAnnotationRequest.setId("1234567890");
        updateAnnotationRequest.setText("舌前三分之二，腹面");
        updateAnnotationRequest.setManualAnnotation("");
        System.out.println(">>>>>>>>param："+JSON.toJSONString(updateAnnotationRequest));
        String annotationResult=apiServerClient.updateAnnotationTokenizePos(updateAnnotationRequest);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>ApiServer<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(JSON.toJSONString(annotationResult));
    }





}
