package cn.malgo.annotation.common.dal.model;

import javax.persistence.*;

@Table(name="CONCEPT")
public class Concept {
    @Id
    private Integer id;

    @Column(name = "standard_name")
    private String standardName;

    @Column(name = "concept_id")
    private String conceptId;

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
     * @return standard_name
     */
    public String getStandardName() {
        return standardName;
    }

    /**
     * @param standardName
     */
    public void setStandardName(String standardName) {
        this.standardName = standardName;
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
}