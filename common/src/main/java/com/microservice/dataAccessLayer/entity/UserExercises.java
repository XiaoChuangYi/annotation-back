package com.microservice.dataAccessLayer.entity;

import java.util.Date;

/**
 * Created by cjl on 2018/5/3.
 */
public class UserExercises {

    private int id;
    private String originText;
    private String practiceAnnotation;
    private String standardAnnotation;
    private String state;
    private int userModifier;
    private Date gmtCreated;
    private Date gmtModified;
    private int anId;
    private String memo;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAnId() {
        return anId;
    }

    public void setAnId(int anId) {
        this.anId = anId;
    }

    public String getOriginText() {
        return originText;
    }

    public void setOriginText(String originText) {
        this.originText = originText;
    }


    public String getStandardAnnotation() {
        return standardAnnotation;
    }

    public void setStandardAnnotation(String standardAnnotation) {
        this.standardAnnotation = standardAnnotation;
    }

    public String getPracticeAnnotation() {
        return practiceAnnotation;
    }

    public void setPracticeAnnotation(String practiceAnnotation) {
        this.practiceAnnotation = practiceAnnotation;
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
