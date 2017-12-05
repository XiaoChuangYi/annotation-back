package cn.malgo.annotation.common.dal.model;

import javax.persistence.*;
import java.util.List;

@Table(name = "TERM")
public class Term {
    @Id
    private Integer id;

    @Column(name = "term_id")
    private String termId;

    @Column(name = "pterm_id")
    private String ptermId;

    @Column(name = "term_code")
    private String termCode;

    @Column(name = "term_type")
    private String termType;

    @Column(name = "term_name")
    private String termName;

    @Column(name = "origin_name")
    private String originName;

    @Column(name = "has_children")
    private Integer hasChildren;

    @Column(name = "label")
    private String label;

    @Column(name = "state")
    private String state;

    @Column(name="concept_id")
    private String conceptId;

    private String standardName;

    public String getStandardName() {
        return standardName;
    }

    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }

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

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getPtermId() {
        return ptermId;
    }

    public void setPtermId(String ptermId) {
        this.ptermId = ptermId;
    }

    public String getTermCode() {
        return termCode;
    }

    public void setTermCode(String termCode) {
        this.termCode = termCode;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
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
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
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

    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }
}