package cn.malgo.annotation.test;

import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.mapper.AnTermMapper;
import cn.malgo.annotation.common.dal.model.AnTerm;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.core.model.enums.CommonStatusEnum;

/**
 * Created by 张钟 on 2017/10/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TermTest {

    @Autowired
    private AnTermMapper       anTermMapper;

    @Autowired
    private AnAtomicTermMapper anAtomicTermMapper;

    @Autowired
    private SequenceGenerator  sequenceGenerator;

    @Test
    public void test() {
        String id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        AnTerm term = new AnTerm();
        term.setId(id);
        term.setState(CommonStatusEnum.ENABLE.name());
        term.setType("type");
        term.setMemo("memo");
        term.setTerm("术语" + id);

        int result = anTermMapper.insert(term);
        System.out.println(result);
    }

    @Test
    public void testAtomicMapper(){

        AnAtomicTerm anAtomicTerm = new AnAtomicTerm();
        anAtomicTerm.setType("310004");
        anAtomicTerm.setTerm("熊");
        anAtomicTerm.setId(sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT));
        int insertResult = anAtomicTermMapper.insert(anAtomicTerm);
        System.out.println(insertResult);
    }
}
