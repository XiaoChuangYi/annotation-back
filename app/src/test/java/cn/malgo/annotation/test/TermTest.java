package cn.malgo.annotation.test;

import cn.malgo.annotation.common.dal.model.Corpus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.mapper.CorpusMapper;
import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.core.model.enums.CommonStatusEnum;
import cn.malgo.common.security.SecurityUtil;

/**
 * Created by 张钟 on 2017/10/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TermTest {

    @Autowired
    private CorpusMapper corpusMapper;

    @Autowired
    private AnAtomicTermMapper anAtomicTermMapper;

    @Autowired
    private SequenceGenerator  sequenceGenerator;

    @Test
    public void test() {
        String id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        Corpus term = new Corpus();
        term.setId(id);
        term.setState(CommonStatusEnum.ENABLE.name());
        term.setType("type");
        term.setMemo("memo");
        term.setTerm("术语" + id);

        int result = corpusMapper.insert(term);
        System.out.println(result);
    }

    @Test
    public void testAtomicMapper() {

        AnAtomicTerm anAtomicTerm = new AnAtomicTerm();
        anAtomicTerm.setType("310004");
        anAtomicTerm.setTerm("熊");
        anAtomicTerm.setId(sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT));
        int insertResult = anAtomicTermMapper.insert(anAtomicTerm);
        System.out.println(insertResult);
    }

    @Test
    public void testDecrypt() {
        String result = SecurityUtil.decryptAESBase64("G/guUYLy1m0m7Kv+OM/ZQw==\n");
        System.out.println(result);
    }


}
