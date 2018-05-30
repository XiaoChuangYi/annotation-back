package com.malgo.vo;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by cjl on 2018/5/17.
 */
public class AnnotationWordPosExerciseBratVO {

    private int     id;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreated;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;

    private int      userModifier;

    private String   memo;

    private String state;

    private JSONObject autoBratData;

    private JSONObject standardBratData;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getUserModifier() {
        return userModifier;
    }

    public void setUserModifier(int userModifier) {
        this.userModifier = userModifier;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public JSONObject getAutoBratData() {
        return autoBratData;
    }

    public void setAutoBratData(JSONObject autoBratData) {
        this.autoBratData = autoBratData;
    }

    public JSONObject getStandardBratData() {
        return standardBratData;
    }

    public void setStandardBratData(JSONObject standardBratData) {
        this.standardBratData = standardBratData;
    }
}
