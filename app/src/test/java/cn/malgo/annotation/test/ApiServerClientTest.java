package cn.malgo.annotation.test;

import java.util.ArrayList;
import java.util.List;

import cn.malgo.annotation.common.service.integration.apiserver.vo.TermVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.malgo.annotation.common.service.integration.apiserver.ApiServerClient;
import cn.malgo.annotation.common.service.integration.apiserver.request.UpdateAnnotationRequest;
import cn.malgo.annotation.common.service.integration.apiserver.result.AnnotationResult;
import cn.malgo.annotation.common.service.integration.apiserver.vo.TermTypeVO;

/**
 * Created by 张钟 on 2017/10/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiServerClientTest {

    @Autowired
    private ApiServerClient apiServerClient;

    @Test
    public void testDocPosTag() {
        String result = apiServerClient.docPosTag("胸部CT");
        System.out.println(result);
    }

    @Test
    public void testPhraseTokenize() {
        String result = apiServerClient.phraseTokenize("胸部CT");
        System.out.println(result);
    }

    @Test
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
        System.out.println(results);
    }

    @Test
    public void testPhraseUpdatePosWithNewTerm() {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", "舌前三分之二，腹面");
        jsonObject.put("new_terms", JSONArray.parseArray(
            "[[\"展神经\",\"Body-structure\"],[\"面\",\"Zone\"],[\"胰头\",\"Body-structure\"]]"));
        jsonObject.put("Token",
            "T1\tToken 0 2\t舌前\nT2\tToken 2 6\t三分之二\nT3\tToken 6 7\t，\nT4\tToken 7 9\t腹面");
        jsonObject.put("Manual", "Manual");
        String result = apiServerClient.phraseUpdatePosWithNewTerm(jsonObject);
        System.out.println(result);
    }

    @Test
    public void testBatchPhraseUpdatePosWithNewTerm() {
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
            .batchPhraseUpdatePosWithNewTerm(annotationRequestList);

        System.out.println(annotationResultList);
    }
}
