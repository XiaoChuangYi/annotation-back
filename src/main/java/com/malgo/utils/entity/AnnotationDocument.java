package com.malgo.utils.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import cn.malgo.core.definition.Entity;

import java.util.List;

/**
 * Created by cjl on 2018/5/24.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnotationDocument {
    private String text;
    private List<RelationEntity> relationEntities;
    private List<Entity> entities;

    public AnnotationDocument(String text){
        this.text=text;
    }
}
