package com.microservice.apiserver.request;

import com.microservice.apiserver.vo.TermTypeVO;

import java.util.List;

/**
 * Created by cjl on 2018/4/11.
 */
public class UpdateAnnotationRequest {
    private String           id;
    private String           text;
    private List<TermTypeVO> newTerms;
    private String           autoAnnotation;
    private String           manualAnnotation = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<TermTypeVO> getNewTerms() {
        return newTerms;
    }

    public void setNewTerms(List<TermTypeVO> newTerms) {
        this.newTerms = newTerms;
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
}
