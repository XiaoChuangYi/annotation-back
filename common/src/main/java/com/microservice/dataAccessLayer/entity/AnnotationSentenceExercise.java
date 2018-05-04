package com.microservice.dataAccessLayer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by cjl on 2018/5/3.
 */
public class AnnotationSentenceExercise {
    private int id;
    private String originText;
    private String standardAnnotation;
    private String autoAnnotation;


    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreated;


    private String memo;


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

    public String getStandardAnnotation() {
        return standardAnnotation;
    }

    public void setStandardAnnotation(String standardAnnotation) {
        this.standardAnnotation = standardAnnotation;
    }

    public String getAutoAnnotation() {
        return autoAnnotation;
    }

    public void setAutoAnnotation(String autoAnnotation) {
        this.autoAnnotation = autoAnnotation;
    }


    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
