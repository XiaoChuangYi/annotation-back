package cn.malgo.annotation.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.malgo.annotation.common.dal.mapper.AnTermAnnotationMapper;
import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;

/**
 * Created by 张钟 on 2017/10/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AnTermAnnotationTest {

    @Autowired
    private AnTermAnnotationMapper anTermAnnotationMapper;

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
}
