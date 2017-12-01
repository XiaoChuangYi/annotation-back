package cn.malgo.annotation.core.service.concept;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.mapper.ConceptMapper;
import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
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
 * Created by cjl on 2017/11/30.
 */
@Service
public class AtomicConceptService {
    @Autowired
    private ConceptMapper conceptMapper;

    @Autowired
    private AnAtomicTermMapper anAtomicTermMapper;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    /**
     *@param id
     *@param originName
     */
    @Transactional
    public void addNewConceptAndUpdateAntomic(String id, String originName){
        List<Concept> conceptList=conceptMapper.selectConceptByStandardName(originName);
        AssertUtil.state(conceptList.size()==0,"术语表中已有该条记录");
        String conceptId=sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        AnAtomicTerm anAtomicTerm=new AnAtomicTerm();
        anAtomicTerm.setId(id);
        anAtomicTerm.setConceptId(conceptId);

        Concept concept=new Concept();
        concept.setStandardName(originName);
        concept.setConceptId(conceptId);

        int insertResult=conceptMapper.insertUseGeneratedKeys(concept);
        AssertUtil.state(insertResult > 0, "新增概念失败");
        int updateResult=anAtomicTermMapper.updateConceptIdByPrimaryKey(conceptId,id);
//        int updateResult=anAtomicTermMapper.updateByPrimaryKeySelective(anAtomicTerm);
        AssertUtil.state(updateResult > 0, "更新原子术语失败");
    }

    /**
     *归入旧的concept,即将当前行的conceptId更新为一个新的conceptId,如果concept表有则更新
     *@param  id
     *@param  conceptId
     */
    @Transactional
    public void updateAtomicTermOrAddConcept(String id,String conceptId,String conceptName){
        AnAtomicTerm anAtomicTerm=new AnAtomicTerm();
        anAtomicTerm.setId(id);
        if(conceptId!=null&&!"".equals(conceptId)) {
            //当前concept表中已有该条记录，直接更新term表中的conceptID
            anAtomicTerm.setConceptId(conceptId);
        }else {
            Concept concept=new Concept();
            String pConceptId=sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
            anAtomicTerm.setConceptId(pConceptId);
            concept.setStandardName(conceptName);
            concept.setConceptId(pConceptId);
            int insertResult=conceptMapper.insertUseGeneratedKeys(concept);
            AssertUtil.state(insertResult > 0, "插入概念失败");
        }
        int updateResult=anAtomicTermMapper.updateByPrimaryKeySelective(anAtomicTerm);
        AssertUtil.state(updateResult > 0, "更新原子术语失败");
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
    /**
     *删除术语
     * @param id
     * @param state
     */
    public  void  deleteAtomicTerm(String id,String state){
        AnAtomicTerm anAtomicTerm=new AnAtomicTerm();
        anAtomicTerm.setState(state);
        anAtomicTerm.setId(id);
        int deleteResult=anAtomicTermMapper.updateByPrimaryKeySelective(anAtomicTerm);
        AssertUtil.state(deleteResult > 0, "删除术语失败");
    }
}
