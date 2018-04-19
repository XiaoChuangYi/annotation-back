package com.microservice.result;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * Created by cjl on 2018/3/30.
 */
public class AnnotationBratVO {
    private String     id;

    private String     state;

    private Date gmtCreated;

    private Date       gmtModified;

    private String      modifier;

    private JSONArray memo;

    private JSONArray  newTerms;

    private JSONObject bratData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public JSONArray getMemo() {
        return memo;
    }

    public void setMemo(JSONArray memo) {
        this.memo = memo;
    }

    public JSONArray getNewTerms() {
        return newTerms;
    }

    public void setNewTerms(JSONArray newTerms) {
        this.newTerms = newTerms;
    }

    public JSONObject getBratData() {
        return bratData;
    }

    public void setBratData(JSONObject bratData) {
        this.bratData = bratData;
    }
}
