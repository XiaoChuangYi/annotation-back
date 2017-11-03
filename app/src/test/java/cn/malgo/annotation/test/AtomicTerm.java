package cn.malgo.annotation.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.core.service.term.AtomicTermBatchService;
import cn.malgo.annotation.core.service.term.AtomicTermService;

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

    @Test
    public void test() {
        String id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        AnAtomicTerm anAtomicTerm = new AnAtomicTerm();
        anAtomicTerm.setId(id);
        anAtomicTerm.setTerm("task value");
        anAtomicTerm.setType("type");

        int result = anAtomicTermMapper.insert(anAtomicTerm);
        System.out.println(result);

    }

    @Test
    public void testBatchCheck() {
        atomicTermBatchService.batchCheckRelation();
    }

    @Test
    public void testSaveAtomicTerm() {
        atomicTermService.saveAtomicTerm("fromAnId", "task", "termType");
    }

    @Test
    public void testUpdateAtomicTerm() {
        atomicTermService.updateAtomicTerm("7312953423647539200", "termType2");
    }

    @Test
    public void testBatchReplace() {
        AnAtomicTerm anAtomicTermOld = atomicTermService.queryByAtomicTermId("10211");
        AnAtomicTerm anAtomicTermNew = new AnAtomicTerm();
        BeanUtils.copyProperties(anAtomicTermOld, anAtomicTermNew);
        anAtomicTermNew.setType("type");

        atomicTermBatchService.batchReplaceAtomicTerm(anAtomicTermNew, anAtomicTermOld);
    }

    @Test
    public void testQueryByTerm(){
        List<AnAtomicTerm> anAtomicTermList =  atomicTermService.queryByAtomicTerm("+");
        System.out.println(anAtomicTermList);
    }
}
