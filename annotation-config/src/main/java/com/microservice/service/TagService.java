package com.microservice.service;

import com.microservice.dataAccessLayer.entity.Tag;
import com.microservice.dataAccessLayer.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by cjl on 2018/4/12.
 */
@Service
public class TagService {

    @Autowired
    private TagMapper tagMapper;

    /**
     *查询所有标签
     */
    public List<Tag> listTag(){
        List<Tag> tagList=tagMapper.listTag();
        return tagList;
    }
    /**
     *新增标签
     */
    public void saveTag(String tagName){
        tagMapper.insertTag(tagName);
    }
    /**
     *新增标签列表
     */
    public  void saveTagAggregate(List<String> tags){
        tagMapper.batchInsertTag(tags);
    }
}
