package cn.malgo.annotation.core.service.term;

import cn.malgo.annotation.common.service.integration.apiserver.vo.TermTypeVO;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.model.enums.CommonStatusEnum;
import cn.malgo.common.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;

import java.util.Date;

/**
 *
 * @author 张钟
 * @date 2017/10/23
 */
@Service
public class AtomicTermService {

    @Autowired
    private SequenceGenerator  sequenceGenerator;

    @Autowired
    private AnAtomicTermMapper anAtomicTermMapper;


    /**
     * 保存原子术语,如果术语已经存在,更新,否则,新增
     * @param termTypeVO
     */
    public void saveAtomicTerm(TermTypeVO termTypeVO){

        saveAtomicTerm(termTypeVO.getTerm(),termTypeVO.getType());

    }

    /**
     * 保存原子术语,如果术语已经存在,更新,否则,新增
     * @param term
     * @param termType
     */
    public void saveAtomicTerm(String term, String termType) {

        String securityTerm = SecurityUtil.cryptAESBase64(term);

        AnAtomicTerm anAtomicTermOld = anAtomicTermMapper.selectByTerm(securityTerm);

        if (anAtomicTermOld == null) {
            String id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
            AnAtomicTerm anAtomicTermNew = new AnAtomicTerm();
            anAtomicTermNew.setId(id);
            anAtomicTermNew.setTerm(securityTerm);
            anAtomicTermNew.setType(termType);
            anAtomicTermNew.setState(CommonStatusEnum.ENABLE.name());

            int insertResult = anAtomicTermMapper.insert(anAtomicTermNew);
            AssertUtil.state(insertResult>0,"保存原子术语失败");
        }else{

            anAtomicTermOld.setType(termType);
            anAtomicTermOld.setTerm(securityTerm);
            anAtomicTermOld.setGmtModified(new Date());

            int updateResult = anAtomicTermMapper.updateByPrimaryKeySelective(anAtomicTermOld);
            AssertUtil.state(updateResult>0,"更新原子术语失败");
        }

    }
}
