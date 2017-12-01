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
     *按条件分页查询术语
     * @param pageNum
     * @param pageSize
     * @param termName
     * @param termType
     * @param label
     */
    public Page<Term> QueryAllByCondition(int pageNum, int pageSize,String termName,String termType,String label,String checked){
        Page<Term> pageInfo= PageHelper.startPage(pageNum,pageSize);
        termMapper.selectTermByCondition(termName,termType,label,checked);
        return  pageInfo;
    }

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
     * 更新术语的conceptId
     * @param id
     * @param conceptId
     */
    public  void  updateTerm(int id,String conceptId){
         Term pTerm=new Term();
         pTerm.setId(id);
         pTerm.setConceptId(conceptId);
         int Result=termMapper.updateByPrimaryKeySelective(pTerm);
         AssertUtil.state(Result > 0, "更新术语conceptId失败");
    }
    /**
     *更新术语
     * @param id
     * @param pTermId
     * @param termName
     */
    public  void updateTerm(int id,String pTermId,String termName){
        Term term=new Term();
        term.setId(id);
        term.setPtermId(pTermId);
        term.setTermName(termName);
        int updateResult=termMapper.updateByPrimaryKeySelective(term);
        AssertUtil.state(updateResult > 0, "更新术语失败");
    }

    /**
     * 更新指定行的label字段
     * @param  id
     * @param  label
     */
    public  void updateTermLabel(int id,String label){
        Term term=new Term();
        term.setId(id);
        term.setLabel(label);
        int updateResult=termMapper.updateByPrimaryKeySelective(term);
        AssertUtil.state(updateResult > 0, "更新术语标签失败");
    }


    /**
     *新增术语
     * @param termId
     * @param pTermId
     * @param termName
     * @param termCode
     * @param termType
     * @param label
     * @param state
     */
    public void insertTerm(String termId,String pTermId,String termName,
                           String termCode,String termType,String originName,String label,String state){
        Term term=new Term();
        term.setTermId(termId);
        term.setPtermId(pTermId);
        term.setTermName(termName);
        term.setOriginName(originName);
        term.setLabel(label);
        term.setTermCode(termCode);
        term.setState(state);
        term.setTermType(termType);
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
