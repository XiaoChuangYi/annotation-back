package cn.malgo.annotation.common.service.integration.apiserver.request;

import cn.malgo.annotation.common.service.integration.apiserver.vo.TermTypeVO;

import java.util.List;

/**
 * Created by 张钟 on 2017/10/18.
 */
public class UpdateAnnotationRequest {
    private String id;
    private String text;
    private List<TermTypeVO> newTerms; // list of pair < word, type >
    private String autoAnnotation;
    private String manualAnnotation;

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
