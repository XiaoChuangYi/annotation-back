package com.microservice.request;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by cjl on 2018/5/23.
 */
public class RelationDelete {
    private String anId;
    private String rTag;

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
    public String check(RelationDelete relationDelete){
        StringBuilder sb=new StringBuilder();
        if(StringUtils.isBlank(relationDelete.getAnId()))
            sb.append("参数anId不能为空");
        if(StringUtils.isBlank(relationDelete.getrTag()))
            sb.append("参数rTag不能为空");
        return sb.toString();
    }
}
