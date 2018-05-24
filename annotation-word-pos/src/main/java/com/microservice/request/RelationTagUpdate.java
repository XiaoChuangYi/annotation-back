package com.microservice.request;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by cjl on 2018/5/23.
 */
public class RelationTagUpdate {

    private String anId;
    private String rTag;
    private String sourceTag;
    private String targetTag;

    public String getAnId() {
        return anId;
    }

    public void setAnId(String anId) {
        this.anId = anId;
    }

    public String getrTag() {
        return rTag;
    }

    public void setrTag(String rTag) {
        this.rTag = rTag;
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

    public String check(RelationTagUpdate relationTagUpdate){
        StringBuilder sb=new StringBuilder();
        if(StringUtils.isBlank(relationTagUpdate.getAnId()))
            sb.append("参数anId不能为空");
        if(StringUtils.isBlank(relationTagUpdate.getrTag()))
            sb.append("参数rTag不能为空");
        if(StringUtils.isBlank(relationTagUpdate.getSourceTag())&&StringUtils.isBlank(relationTagUpdate.getTargetTag()))
            sb.append("参数sourceTag和targetTag不能同时为空！");
        return sb.toString();
    }
}
