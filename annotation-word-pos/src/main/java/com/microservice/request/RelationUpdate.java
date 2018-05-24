package com.microservice.request;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by cjl on 2018/5/23.
 */
public class RelationUpdate {

    private String anId;
    private String rTag;
    private String relation;

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

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String check(RelationUpdate relationUpdate){
        StringBuilder sb=new StringBuilder();
        if(StringUtils.isBlank(relationUpdate.getAnId()))
            sb.append("参数anId不能为空");
        if(StringUtils.isBlank(relationUpdate.getrTag()))
            sb.append("参数rTag不能为空");
        if(StringUtils.isBlank(relationUpdate.getRelation()))
            sb.append("参数relation不能为空");
        return sb.toString();
    }
}
