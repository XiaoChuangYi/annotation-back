package com.microservice.dto;

import java.io.Serializable;

/**
 * Created by cjl on 2018/5/23.
 */

public class RelationEntity implements Serializable{
    private String tag;
    private String type;
    private String source;
    private String target;
    private String sourceTag;
    private String targetTag;


    public RelationEntity(RelationEntity relationEntity){
        this.tag=relationEntity.tag;
        this.type=relationEntity.type;
        this.source=relationEntity.source;
        this.target=relationEntity.target;
        this.sourceTag=relationEntity.sourceTag;
        this.targetTag=relationEntity.targetTag;
    }

    public RelationEntity(String tag, String type,String sourceTag,String targetTag, String source, String target) {
        this.tag = tag;
        this.type = type;
        this.sourceTag=sourceTag;
        this.targetTag=targetTag;
        this.source = source;
        this.target = target;
    }

    public RelationEntity() {
    }



    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getSourceTag() {
        return sourceTag;
    }

    public void setSourceTag(String sourceTag) {
        this.sourceTag = sourceTag;
    }

    public String getTargetTag() {
        return targetTag;
    }

    public void setTargetTag(String targetTag) {
        this.targetTag = targetTag;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
