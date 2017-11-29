package cn.malgo.annotation.core.service.concept;

import cn.malgo.annotation.common.dal.mapper.ConceptMapper;
import cn.malgo.annotation.common.dal.model.Concept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by cjl on 2017/11/29.
 */
@Service
public class ConceptService {
    @Autowired
    private ConceptMapper conceptMapper;

    /**
     *新增concept
     * @param  conceptName
     */
    public  void  insertConcept(String conceptName){
        Concept concept=new Concept();
        concept.setStandardName(conceptName);
        conceptMapper.insertUseGeneratedKeys(concept);
    }
    /**
     *更新concept
     * @param id
     * @param conceptName
     */
    public void updateConcept(int id,String conceptName){
        Concept concept=new Concept();
        concept.setId(id);
        concept.setStandardName(conceptName);
        conceptMapper.updateByPrimaryKey(concept);
    }
    /**
     *查询concept
     */
    public List<Concept> selectAllConcept(){
        List<Concept> conceptList=conceptMapper.selectAll();
        return conceptList;
    }

}
