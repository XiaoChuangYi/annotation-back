package cn.malgo.annotation.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.pagehelper.Page;

import cn.malgo.annotation.common.dal.mapper.AnTermAnnotationMapper;
import cn.malgo.annotation.common.dal.model.AnTerm;
import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.core.model.annotation.TermAnnotationModel;
import cn.malgo.annotation.core.model.convert.AnnotationConvert;
import cn.malgo.annotation.core.service.annotation.AnnotationBatchService;
import cn.malgo.annotation.core.service.annotation.AnnotationService;

/**
 * Created by 张钟 on 2017/10/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AnTermAnnotationTest {

    @Autowired
    private AnTermAnnotationMapper anTermAnnotationMapper;

    @Autowired
    private AnnotationService      annotationService;

    @Autowired
    private AnnotationBatchService annotationBatchService;

    @Autowired
    private SequenceGenerator      sequenceGenerator;

    @Test
    public void test() {

        String id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        AnTermAnnotation anTermAnnotation = new AnTermAnnotation();
        anTermAnnotation.setAutoAnnotation("annotation");
        anTermAnnotation.setFinalAnnotation("finalAnnotation");
        anTermAnnotation.setId(id);
        anTermAnnotation.setManualAnnotation("manualAnnotation");
        anTermAnnotation.setMemo("memo");
        anTermAnnotation.setNewTerms("newTerms");
        anTermAnnotation.setTermId("termId");

        int saveResult = anTermAnnotationMapper.insert(anTermAnnotation);
        System.out.println(saveResult);
    }

    @Test
    public void testSelectById() {
        AnTermAnnotation anTermAnnotation = anTermAnnotationMapper
            .selectByPrimaryKey("7307448151378575360");
        System.out.println(anTermAnnotation);
    }

    @Test
    public void testQueryOnePage() {
        Page<AnTermAnnotation> page = annotationService
            .queryOnePageThroughApiServer("11111111111111111111", 0, 10);
        System.out.println(page);
    }

    @Test
    public void autoAnnotation() {
        annotationService.autoAnnotationByTermId("7307206713306857472");
    }

    @Test
    public void testManualAnnotation() {
        //        annotationService.autoAnnotationByAnId("7307559044527505408", "12",
        //            "[[\"展神经\",\"Body-structure\"],[\"面\",\"Zone\"],[\"胰头\",\"Body-structure\"]]");
    }

    @Test
    public void testBatchAutoAnnotation() {

        List<AnTerm> termList = new ArrayList<>();
        AnTerm termVO = new AnTerm();
        termVO.setId("123456");
        termVO.setTerm("胸部CT");
        termList.add(termVO);

        AnTerm termVO2 = new AnTerm();
        termVO2.setId("456789");
        termVO2.setTerm("舌前三分之二，腹面");
        termList.add(termVO2);

        annotationService.autoAnnotationByTermList(termList);

        System.out.println("123456");
    }

    @Test
    public void finish() {
        annotationService.finishAnnotation("7307559044527505408");
    }

    @Test
    public void getNewTag() {
        AnTermAnnotation anTermAnnotation = annotationService.queryByAnId("7309720136175042560");
        String newAnnotation = AnnotationConvert.addNewTag(anTermAnnotation.getManualAnnotation(),
            "body", "12", "23", "身体");
        System.out.println(newAnnotation);
    }

    @Test
    public void deleteTag() {
        AnTermAnnotation anTermAnnotation = annotationService.queryByAnId("7309720136175042560");
        String newAnnotation = AnnotationConvert.deleteTag(anTermAnnotation.getManualAnnotation(),
            "T3");
        System.out.println(newAnnotation);
    }

    @Test
    public void testConvertToModel() {
        String text = "T1\tBody-structure-unconfirmed 2 5\t支气管\n"
                      + "T2\tBody-structure-unconfirmed 5 7\t动脉\n"
                      + "T3\tTreatment-unconfirmed 9 11\t化疗\n"
                      + "T4\tTreatment-unconfirmed 7 9\t灌注\n" + "T5\tSpace-unconfirmed 0 1\t左\n"
                      + "T6\tProcedure-unconfirmed 11 12\t术\n"
                      + "T7\tBody-structure-unconfirmed 1 2\t肺";
        List<TermAnnotationModel> termAnnotationModelList = AnnotationConvert
            .convertAnnotationModelList(text);
        System.out.println(termAnnotationModelList);
    }

    @Test
    public void testBatchCheckAmbiguity() {
        annotationBatchService.batchCheckAmbiguity();
    }

}
