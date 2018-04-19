package com.microservice.service;

import com.microservice.dataAccessLayer.entity.ConceptShow;
import com.microservice.dataAccessLayer.mapper.ConceptShowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by cjl on 2018/4/12.
 */
@Service
public class ConceptShowService {

    @Autowired
    private ConceptShowMapper conceptShowMapper;

    /**
     * 根据传入的conceptId，查询所有父ID为传入conceptId的记录数
     * @param conceptId
     */
    public List<ConceptShow> listChildrenConceptShowByConcepId(String conceptId){
        List<ConceptShow> conceptShowList = conceptShowMapper.listConceptShowByConceptId(conceptId);
        return conceptShowList;
    }
}
