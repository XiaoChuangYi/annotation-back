package com.microservice.dataAccessLayer.entity;

/**
 * Created by cjl on 2018/4/11.
 */
public class ConceptShow {

    private String conceptId;
    private String pconceptId;
    private String conceptCode;
    private String conceptType;
    private String conceptName;
    private String atomicId;
    private int hasChildren;
    private String state;

    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }

    public String getPconceptId() {
        return pconceptId;
    }

    public void setPconceptId(String pconceptId) {
        this.pconceptId = pconceptId;
    }

    public String getConceptCode() {
        return conceptCode;
    }

    public void setConceptCode(String conceptCode) {
        this.conceptCode = conceptCode;
    }

    public String getConceptType() {
        return conceptType;
    }

    public void setConceptType(String conceptType) {
        this.conceptType = conceptType;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public String getAtomicId() {
        return atomicId;
    }

    public void setAtomicId(String atomicId) {
        this.atomicId = atomicId;
    }

    public int getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(int hasChildren) {
        this.hasChildren = hasChildren;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
