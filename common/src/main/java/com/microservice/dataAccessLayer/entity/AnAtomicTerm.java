package com.microservice.dataAccessLayer.entity;

import java.util.Date;

/**
 * Created by cjl on 2018/4/11.
 */
public class AnAtomicTerm {
    private String id;
    private String term;
    private String type;
    private String state;
    private String fromAnid;
    private Date gmtCreated;
    private Date gmtModified;
    private String conceptId;
    private String standardName;


    public String getStandardName() {
        return standardName;
    }

    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFromAnid() {
        return fromAnid;
    }

    public void setFromAnid(String fromAnid) {
        this.fromAnid = fromAnid;
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

    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }
}
