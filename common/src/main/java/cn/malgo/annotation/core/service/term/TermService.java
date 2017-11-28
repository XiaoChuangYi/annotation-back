package cn.malgo.annotation.core.service.term;

import cn.malgo.annotation.common.dal.mapper.TermMapper;
import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.model.Term;
import cn.malgo.annotation.common.util.AssertUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by cjl on 2017/11/28.
 */
@Service
public class TermService {

    @Autowired
    private TermMapper termMapper;

    /**
     *分页查询术语
     * @param pageNum
     * @param pageSize
     */
    public Page<Term> QueryAll(int pageNum, int pageSize){
        Page<Term> pageInfo= PageHelper.startPage(pageNum,pageSize);
        termMapper.selectEnableTerm();
        return  pageInfo;
    }

    /**
     *更新术语
     * @param id
     * @param pConceptId
     * @param conceptName
     */
    public  void updateTerm(int id,String pConceptId,String conceptName){
        Term term=new Term();
        term.setId(id);
        term.setPconceptId(pConceptId);
        term.setConceptName(conceptName);
        int updateResult=termMapper.updateByPrimaryKeySelective(term);
        AssertUtil.state(updateResult > 0, "更新术语失败");
    }
    /**
     *新增术语
     * @param conceptId
     * @param pConceptId
     * @param conceptName
     * @param conceptCode
     * @param conceptType
     * @param category
     * @param state
     */
    public void insertTerm(String conceptId,String pConceptId,String conceptName,
                           String conceptCode,String conceptType,String originName,String category,String state){
        Term term=new Term();
        term.setConceptId(conceptId);
        term.setPconceptId(pConceptId);
        term.setConceptName(conceptName);
        term.setOriginName(originName);
        term.setCategory(category);
        term.setConceptCode(conceptCode);
        term.setState(state);
        term.setConceptType(conceptType);
        int insertResult=termMapper.insertUseGeneratedKeys(term);
        AssertUtil.state(insertResult > 0, "新增术语失败");
    }
    /**
     *删除术语
     * @param id
     * @param state
     */
    public  void  deleteTerm(int id,String state){
        Term term=new Term();
        term.setState(state);
        term.setId(id);
        int deleteResult=termMapper.updateByPrimaryKeySelective(term);
        AssertUtil.state(deleteResult > 0, "删除术语失败");
    }

}
