package cn.malgo.annotation.core.service.concept;

import cn.malgo.annotation.common.dal.mapper.ConceptMapper;
import cn.malgo.annotation.common.dal.mapper.TermMapper;
import cn.malgo.annotation.common.dal.model.Concept;
import cn.malgo.annotation.common.dal.model.Term;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.common.util.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by cjl on 2017/11/29.
 */
@Service
public class MixtureService {

    @Autowired
    private ConceptMapper conceptMapper;

    @Autowired
    private TermMapper termMapper;

    @Autowired
    private SequenceGenerator sequenceGenerator;
    /**
     *@param id
     *@param originName
     */
    @Transactional
    public void addNewConcept(int id, String originName){
        String conceptId=sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        Term termNew=new Term();
        termNew.setId(id);
        termNew.setConceptId(conceptId);

        Concept concept=new Concept();
        concept.setStandardName(originName);
        concept.setConceptId(conceptId);

        int insertResult=conceptMapper.insertUseGeneratedKeys(concept);
        AssertUtil.state(insertResult > 0, "新增概念失败");
        int updateResult=termMapper.updateByPrimaryKeySelective(termNew);
        AssertUtil.state(updateResult > 0, "更新术语失败");
    }

    /**
     *归入旧的concept,即将当前行的conceptId更新为一个新的conceptId,如果concept表有则更新
     *@param  id
     *@param  conceptId
     */
    @Transactional
    public void updateTermOrAddConcept(int id,String conceptId,String conceptName){
        Term term=new Term();
        term.setId(id);
        if(conceptId!=null&&!"".equals(conceptId)) {
            //当前concept表中已有该条记录，直接更新term表中的conceptID
            term.setConceptId(conceptId);
        }else {
            String pConceptId=sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
            term.setConceptId(pConceptId);
            Concept concept=new Concept();
            concept.setStandardName(conceptName);
            concept.setConceptId(pConceptId);
            int insertResult=conceptMapper.insertUseGeneratedKeys(concept);
            AssertUtil.state(insertResult > 0, "插入概念失败");
        }
        int updateResult=termMapper.updateByPrimaryKeySelective(term);
        AssertUtil.state(updateResult > 0, "更新术语失败");
    }
    /**
     *修改标注名称
     * @param standName
     * @param conceptId
     */
    public void updateStandNameofConcept(String standName,String conceptId){
        Concept concept=new Concept();
        concept.setStandardName(standName);
        Concept oldConcept=conceptMapper.selectConceptByConceptId(conceptId);
        if(oldConcept==null){
            //新增该条数据
        }else{
            concept.setId(oldConcept.getId());
            int updateResult=conceptMapper.updateByPrimaryKeySelective(concept);
            AssertUtil.state(updateResult > 0, "更新术语名称失败");
        }

    }

    /**
     *查询concept
     */
    public List<Concept> selectAllConcept(){
        List<Concept> conceptList=conceptMapper.selectAll();
        return conceptList;
    }

    /**
     *根据conceptId查询单条concept
     */
    public  Concept selectOneConcept(String conceptId){
        Concept concept=conceptMapper.selectConceptByConceptId(conceptId);
        return  concept;
    }
}
