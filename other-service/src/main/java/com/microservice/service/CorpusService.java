package com.microservice.service;

import com.microservice.dataAccessLayer.entity.Corpus;
import com.microservice.dataAccessLayer.mapper.CorpusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by cjl on 2018/4/12.
 */
@Service
public class CorpusService {

    @Autowired
    private CorpusMapper corpusMapper;

    /**
     * 批量更新corpus表中的类型type
     * @param typeOld
     * @param typeNew
     */
    public  void  batchUpdateCorpusType(String typeOld,String typeNew){
        Corpus corpus=new Corpus();
        corpus.setType(typeOld);
        List<Corpus> corpusList = corpusMapper.listCorpusByCondition(corpus);
        if(corpusList.size()>0){
            List<String> idsList=new LinkedList<>();
            for(int k = 0; k< corpusList.size(); k++){
                idsList.add(corpusList.get(k).getId());
            }
            corpusMapper.batchUpdateCorpusByIdArr(idsList,typeNew);
        }
    }
}
