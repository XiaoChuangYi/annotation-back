package com.microservice.dto;

import cn.malgo.core.definition.Entity;

import java.util.List;

/**
 * Created by cjl on 2018/5/23.
 */
public class AnnoDocument {

    private String text;
    private List<RelationEntity> relationEntities;
    private List<Entity> entities;


    public AnnoDocument(String text) {
        this.text = text;
    }

    public AnnoDocument() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<RelationEntity> getRelationEntities() {
        return relationEntities;
    }

    public void setRelationEntities(List<RelationEntity> relationEntities) {
        this.relationEntities = relationEntities;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }
}
