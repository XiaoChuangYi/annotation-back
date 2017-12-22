package cn.malgo.annotation.test;

import cn.malgo.annotation.common.dal.model.Annotation;
import cn.malgo.annotation.core.service.annotation.AnnotationService;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.core.service.corpus.AtomicTermBatchService;
import cn.malgo.annotation.core.service.corpus.AtomicTermService;

import java.util.List;

/**
 * Created by 张钟 on 2017/10/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AtomicTerm {

    @Autowired
    private AnAtomicTermMapper     anAtomicTermMapper;

    @Autowired
    private SequenceGenerator      sequenceGenerator;

    @Autowired
    private AtomicTermBatchService atomicTermBatchService;

    @Autowired
    private AtomicTermService      atomicTermService;

    @Autowired
    private AnnotationService annotationService;

//    @Test
//    public void test() {
//        String id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
//        AnAtomicTerm anAtomicTerm = new AnAtomicTerm();
//        anAtomicTerm.setId(id);
//        anAtomicTerm.setTerm("task value");
//        anAtomicTerm.setType("type");
//
//        int result = anAtomicTermMapper.insert(anAtomicTerm);
//        System.out.println(result);
//
//    }
//
//    @Test
//    public void testBatchCheck() {
//        atomicTermBatchService.batchCheckRelation();
//    }
//
//    @Test
//    public void testSaveAtomicTerm() {
//        atomicTermService.saveAtomicTerm("fromAnId", "task", "termType");
//    }
//
//    @Test
//    public void testUpdateAtomicTerm() {
//        atomicTermService.updateAtomicTerm("7312953423647539200", "termType2");
//    }
//
//    @Test
//    public void testBatchReplace() {
//        AnAtomicTerm anAtomicTermOld = atomicTermService.queryByAtomicTermId("10211");
//        AnAtomicTerm anAtomicTermNew = new AnAtomicTerm();
//        BeanUtils.copyProperties(anAtomicTermOld, anAtomicTermNew);
//        anAtomicTermNew.setType("type");
//
//        atomicTermBatchService.batchReplaceAtomicTerm(anAtomicTermNew, anAtomicTermOld);
//    }
//
//    @Test
//    public void testQueryByTerm(){
////        List<AnAtomicTerm> anAtomicTermList =  atomicTermService.queryByAtomicTerm("+");
//        List<AnAtomicTerm> anAtomicTermList =  atomicTermService.fuzzyQueryOnePage("风","Disease",1,250);
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>打印<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//        System.out.println(JSON.parseArray(JSON.toJSONString(anAtomicTermList)));
//    }
    @Test
    public void test(){
        List<String> idsList=annotationService.getAnnotationIDsByCondition("FINISH","",10);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>打印<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(JSON.parseArray(JSON.toJSONString(idsList)));
    }
}
