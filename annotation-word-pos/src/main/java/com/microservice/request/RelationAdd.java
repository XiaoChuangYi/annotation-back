package com.microservice.request;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by cjl on 2018/5/23.
 */
public class RelationAdd {
    private String anId;
    private String sourceTag;
    private String targetTag;
    private String relation;

    public String getAnId() {
        return anId;
    }

    public void setAnId(String anId) {
        this.anId = anId;
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

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String check(RelationAdd relationAdd) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isBlank(relationAdd.getAnId()))
            sb.append("参数anId不能为空");

        if (StringUtils.isBlank(relationAdd.getRelation()))
            sb.append("参数relation不能为空");

        if (StringUtils.isBlank(relationAdd.getSourceTag()))
            sb.append("参数sourceTag不能为空");

        if (StringUtils.isBlank(relationAdd.getTargetTag()))
            sb.append("参数targetTag不能为空");
        return sb.toString();

    }
}
