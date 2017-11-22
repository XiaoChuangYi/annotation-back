package cn.malgo.annotation.core.service.type;

import cn.malgo.annotation.common.dal.mapper.ConceptMapper;
import cn.malgo.annotation.common.dal.model.Concept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by cjl on 2017/11/22.
 */
@Service
public class ConceptService {
    @Autowired
    private ConceptMapper conceptMapper;

//    public  void insertBatch(List< Concept> listConcept){
//        conceptMapper.insertList(listConcept);
//    }
//    public Concept selecAll(){
//        Concept conceptold=new Concept();
//        conceptold.setConceptId("ICD10CH000018");
//        return conceptMapper.selectOne(conceptold);
//    }
    /**
     * 根据传入的conceptId，查询所有父ID为传入conceptId的记录数
     * @param conceptId
     */
    public List<Concept> selectAllByConcepId(String conceptId){
        List<Concept> conceptList=conceptMapper.selectConceptByConceptId(conceptId);
        return  conceptList;
    }


}
