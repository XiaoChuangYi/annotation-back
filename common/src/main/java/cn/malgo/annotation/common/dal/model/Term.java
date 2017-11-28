package cn.malgo.annotation.common.dal.model;

import javax.persistence.*;

@Table(name = "TERM")
public class Term {
    @Id
    private Integer id;

    @Column(name = "concept_id")
    private String conceptId;

    @Column(name = "pconcept_id")
    private String pconceptId;

    @Column(name = "concept_code")
    private String conceptCode;

    @Column(name = "concept_type")
    private String conceptType;

    @Column(name = "concept_name")
    private String conceptName;

    @Column(name = "origin_name")
    private String originName;

    @Column(name = "has_children")
    private Integer hasChildren;

    @Column(name = "category")
    private String category;

    @Column(name = "state")
    private String state;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return concept_id
     */
    public String getConceptId() {
        return conceptId;
    }

    /**
     * @param conceptId
     */
    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }

    /**
     * @return pconcept_id
     */
    public String getPconceptId() {
        return pconceptId;
    }

    /**
     * @param pconceptId
     */
    public void setPconceptId(String pconceptId) {
        this.pconceptId = pconceptId;
    }

    /**
     * @return concept_code
     */
    public String getConceptCode() {
        return conceptCode;
    }

    /**
     * @param conceptCode
     */
    public void setConceptCode(String conceptCode) {
        this.conceptCode = conceptCode;
    }

    /**
     * @return concept_type
     */
    public String getConceptType() {
        return conceptType;
    }

    /**
     * @param conceptType
     */
    public void setConceptType(String conceptType) {
        this.conceptType = conceptType;
    }

    /**
     * @return concept_name
     */
    public String getConceptName() {
        return conceptName;
    }

    /**
     * @param conceptName
     */
    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    /**
     * @return has_children
     */
    public Integer getHasChildren() {
        return hasChildren;
    }

    /**
     * @param hasChildren
     */
    public void setHasChildren(Integer hasChildren) {
        this.hasChildren = hasChildren;
    }

    /**
     * @return category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }
}