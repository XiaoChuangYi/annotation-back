package com.microservice.dataAccessLayer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by cjl on 2018/5/17.
 */
public class UserWordExercise {
    private int id;
    private String originText;
    private String practiceAnnotation;
    private String standardAnnotation;
    private String state;
    private int userModifier;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreated;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;
    private int anId;
    private String memo;

    private String accountName;

    public UserWordExercise() {
    }

    public UserWordExercise(String originText, String practiceAnnotation, String state, int userModifier, Date gmtCreated, Date gmtModified, int anId) {
        this.originText = originText;
        this.practiceAnnotation = practiceAnnotation;
        this.state = state;
        this.userModifier = userModifier;
        this.gmtCreated = gmtCreated;
        this.gmtModified = gmtModified;
        this.anId = anId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginText() {
        return originText;
    }

    public void setOriginText(String originText) {
        this.originText = originText;
    }

    public String getPracticeAnnotation() {
        return practiceAnnotation;
    }

    public void setPracticeAnnotation(String practiceAnnotation) {
        this.practiceAnnotation = practiceAnnotation;
    }

    public String getStandardAnnotation() {
        return standardAnnotation;
    }

    public void setStandardAnnotation(String standardAnnotation) {
        this.standardAnnotation = standardAnnotation;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getUserModifier() {
        return userModifier;
    }

    public void setUserModifier(int userModifier) {
        this.userModifier = userModifier;
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

    public int getAnId() {
        return anId;
    }

    public void setAnId(int anId) {
        this.anId = anId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
