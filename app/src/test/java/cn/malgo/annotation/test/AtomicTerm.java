package cn.malgo.annotation.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.core.service.term.AtomicTermService;

/**
 * Created by 张钟 on 2017/10/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AtomicTerm {

    @Autowired
    private AnAtomicTermMapper anAtomicTermMapper;

    @Autowired
    private SequenceGenerator  sequenceGenerator;

    @Autowired
    private AtomicTermService  atomicTermService;

    @Test
    public void test() {
        String id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        AnAtomicTerm anAtomicTerm = new AnAtomicTerm();
        anAtomicTerm.setId(id);
        anAtomicTerm.setTerm("term value");
        anAtomicTerm.setType("type");

        int result = anAtomicTermMapper.insert(anAtomicTerm);
        System.out.println(result);

    }

    @Test
    public  void testBatchCheck(){
        atomicTermService.batchCheckRelation();
    }
}
