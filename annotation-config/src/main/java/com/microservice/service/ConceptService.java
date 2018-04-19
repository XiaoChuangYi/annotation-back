package com.microservice.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.microservice.dataAccessLayer.entity.Concept;
import com.microservice.dataAccessLayer.entity.Term;
import com.microservice.dataAccessLayer.mapper.ConceptMapper;
import com.microservice.dataAccessLayer.mapper.TermMapper;
import com.microservice.enums.CodeGenerateTypeEnum;
import com.microservice.sequence.SequenceGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by cjl on 2018/4/12.
 */
@Service
public class ConceptService {

    @Autowired
    private ConceptMapper conceptMapper;


    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private TermMapper termMapper;

    /**
     *查询concept
     */
    public List<Concept> listConcept(){
        List<Concept> conceptList=conceptMapper.listConceptByCondition(new Concept());
        return conceptList;
    }
    /**
     *分页查询concept
     * @param pageNum
     * @param pageSize
     */
    public Page<Concept> listConceptByPagingCondition(int pageNum, int pageSize, String standardName){
        Page<Concept> pageInfo = PageHelper.startPage(pageNum, pageSize);
        Concept concept=new Concept();
        concept.setStandardName(standardName);
        conceptMapper.listConceptByCondition(concept);
        return pageInfo;
    }

    /**
     *根据conceptId查询单条concept
     */
    public  Concept getConceptByConceptId(String conceptId){
        Concept paramConcept=new Concept();
        paramConcept.setConceptId(conceptId);
        Concept concept=conceptMapper.listConceptByCondition(paramConcept).get(0);
        return  concept;
    }
    /**
     *@param id
     *@param standardName
     */
    @Transactional
    public void saveConcept(int id, String standardName){
        Concept paramConcept=new Concept();
        paramConcept.setStandardName(standardName);
        List<Concept> conceptList=conceptMapper.listConceptByCondition(paramConcept);
        if(conceptList.size()>=0)
            return;
        String conceptId=sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        Term termNew=new Term();
        termNew.setId(id);
        termNew.setConceptId(conceptId);

        Concept concept=new Concept();
        concept.setStandardName(standardName);
        concept.setConceptId(conceptId);

        conceptMapper.insertConceptSelective(concept);
        termMapper.updateTermSelective(termNew);
    }

    /**
     *归入旧的concept,即将当前行的conceptId更新为一个新的conceptId,如果concept表有则更新
     *@param  id
     *@param  conceptId
     */
    @Transactional
    public void updateTermOrSaveConcept(int id,String conceptId,String conceptName){
        Term term=new Term();
        term.setId(id);
        if(StringUtils.isNotBlank(conceptId)) {
            //当前concept表中已有该条记录，直接更新term表中的conceptID
            term.setConceptId(conceptId);
        }else {
            String pConceptId=sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
            term.setConceptId(pConceptId);
            Concept concept=new Concept();
            concept.setStandardName(conceptName);
            concept.setConceptId(pConceptId);
            conceptMapper.insertConceptSelective(concept);
        }
        termMapper.updateTermSelective(term);
    }
    /**
     *修改标注名称
     * @param standName
     * @param conceptId
     */
    public void updateConceptStandardName(String standName,String conceptId){
        Concept paramConcept=new Concept();
        paramConcept.setConceptId(conceptId);
        Concept oldConcept=conceptMapper.listConceptByCondition(paramConcept).get(0);
        if(oldConcept!=null){
            Concept concept=new Concept();
            concept.setStandardName(standName);
            concept.setId(oldConcept.getId());
            conceptMapper.updateConceptSelective(concept);
        }
    }
}
