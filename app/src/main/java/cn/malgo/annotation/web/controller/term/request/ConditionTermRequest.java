package cn.malgo.annotation.web.controller.term.request;

import cn.malgo.annotation.web.request.PageRequest;

/**
 * Created by cjl on 2017/11/30.
 */
public class ConditionTermRequest extends PageRequest {
    private String termName;
    private String termType;
    private String label;
    private String checked;
    private String conceptId;
    private String originName;

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

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
