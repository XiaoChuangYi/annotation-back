package cn.malgo.annotation.common.dal.model;

import javax.persistence.*;

@Table(name = "CONCEPT_SHOW")
public class ConceptShow {
    @Id
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

    @Column(name = "has_children")
    private Integer hasChildren;

    @Column(name = "atomic_id")
    private String atomicId;

    @Column(name="state")
    private String state;

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
     * @return atomic_id
     */
    public String getAtomicId() {
        return atomicId;
    }

    /**
     * @param atomicId
     */
    public void setAtomicId(String atomicId) {
        this.atomicId = atomicId;
    }

    /**
     * @return state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state
     */
    public void setState(String state) {
        this.state = state;
    }
}