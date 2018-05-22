package com.microservice.vo;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by cjl on 2018/5/17.
 */
public class UserWordExerciseBratVO {

    private int     id;

    private String     state;


    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreated;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;

    private int      userModifier;

    private int anId;

    private String   memo;

    private String accountName;

    private JSONObject practiceBratData;

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

    public int getAnId() {
        return anId;
    }

    public void setAnId(int anId) {
        this.anId = anId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public JSONObject getPracticeBratData() {
        return practiceBratData;
    }

    public void setPracticeBratData(JSONObject practiceBratData) {
        this.practiceBratData = practiceBratData;
    }

    public JSONObject getStandardBratData() {
        return standardBratData;
    }

    public void setStandardBratData(JSONObject standardBratData) {
        this.standardBratData = standardBratData;
    }
}
