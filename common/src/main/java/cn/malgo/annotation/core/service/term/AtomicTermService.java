package cn.malgo.annotation.core.service.term;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.common.service.integration.apiserver.vo.TermTypeVO;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.model.enums.CommonStatusEnum;
import cn.malgo.common.security.SecurityUtil;

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
     * @param fromAnId 新词来源的标注ID
     */
    public void saveAtomicTerm(String fromAnId,TermTypeVO termTypeVO) {

        saveAtomicTerm(fromAnId,termTypeVO.getTerm(), termTypeVO.getType());

    }

    /**
     * 保存原子术语,如果术语已经存在,更新,否则,新增
     * @param fromAnId 新词来源的标注ID
     * @param term
     * @param termType
     */
    public void saveAtomicTerm(String fromAnId,String term, String termType) {

        String securityTerm = SecurityUtil.cryptAESBase64(term);

        AnAtomicTerm anAtomicTermOld = anAtomicTermMapper.selectByTerm(securityTerm);

        if (anAtomicTermOld == null) {
            String id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
            AnAtomicTerm anAtomicTermNew = new AnAtomicTerm();
            anAtomicTermNew.setId(id);
            anAtomicTermNew.setFromAnId(fromAnId);
            anAtomicTermNew.setTerm(securityTerm);
            anAtomicTermNew.setType(termType);
            anAtomicTermNew.setState(CommonStatusEnum.ENABLE.name());

            int insertResult = anAtomicTermMapper.insert(anAtomicTermNew);
            AssertUtil.state(insertResult > 0, "保存原子术语失败");
        } else {

            anAtomicTermOld.setType(termType);
            anAtomicTermOld.setTerm(securityTerm);
            anAtomicTermOld.setFromAnId(fromAnId);
            anAtomicTermOld.setGmtModified(new Date());

            int updateResult = anAtomicTermMapper.updateByPrimaryKeySelective(anAtomicTermOld);
            AssertUtil.state(updateResult > 0, "更新原子术语失败");
        }

    }

    /**
     * 批量加密
     */
    public void batchCrypt() {

        List<AnAtomicTerm> anAtomicTermList = anAtomicTermMapper.selectAll();

        for (AnAtomicTerm anAtomicTerm : anAtomicTermList) {
            if (anAtomicTerm.getTerm().length() < 16) {
                String securityTerm = SecurityUtil.cryptAESBase64(anAtomicTerm.getTerm());
                anAtomicTerm.setTerm(securityTerm);
                anAtomicTermMapper.updateByPrimaryKeySelective(anAtomicTerm);
            }
        }

    }
}
