package com.microservice.dataAccessLayer.entity;

import java.util.Date;

/**
 * Created by cjl on 2018/3/30.
 */
public class Annotation {

    private String id;
    private String  termId;
    private String term;
    private String finalAnnotation;
    private String autoAnnotation;
    private String manualAnnotation;
    private String newTerms;
    private String state;
    private String modifier;
    private Date gmtCreated;
    private Date gmtModified;
    private String memo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getFinalAnnotation() {
        return finalAnnotation;
    }

    public void setFinalAnnotation(String finalAnnotation) {
        this.finalAnnotation = finalAnnotation;
    }

    public String getAutoAnnotation() {
        return autoAnnotation;
    }

    public void setAutoAnnotation(String autoAnnotation) {
        this.autoAnnotation = autoAnnotation;
    }

    public String getManualAnnotation() {
        return manualAnnotation;
    }

    public void setManualAnnotation(String manualAnnotation) {
        this.manualAnnotation = manualAnnotation;
    }

    public String getNewTerms() {
        return newTerms;
    }

    public void setNewTerms(String newTerms) {
        this.newTerms = newTerms;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
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
