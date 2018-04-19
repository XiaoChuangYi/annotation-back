package com.microservice.dataAccessLayer.entity;

/**
 * Created by cjl on 2018/4/11.
 */
public class Concept {
    private int id;
    private String standardName;
    private String conceptId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStandardName() {
        return standardName;
    }

    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }

    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }
}
