package cn.malgo.annotation.common.dal.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "AN_TERM_ANNOTATION")
public class AnTermAnnotation {
    @Id
    @Column(name = "ID")
    private String id;

    /**
     * 术语ID
     */
    @Column(name = "TERM_ID")
    private String termId;

    /**
     * 术语的状态:
    INIT("待人工标注"),
    PROCESSING("标注中"),
    FINISH("标注完成");
     */
    @Column(name = "STATE")
    private String state;

    /**
     * 修改人
     */
    @Column(name = "MODIFIER")
    private String modifier;

    /**
     * 创建时间
     */
    @Column(name = "GMT_CREATED")
    private Date gmtCreated;

    /**
     * 修改时间
     */
    @Column(name = "GMT_MODIFIED")
    private Date gmtModified;

    @Column(name = "MEMO")
    private String memo;

    /**
     * 术语内容
     */
    @Column(name = "TERM")
    private String term;

    /**
     * 自动标注结果
     */
    @Column(name = "AUTO_ANNOTATION")
    private String autoAnnotation;

    /**
     * 手工标注结果
     */
    @Column(name = "MANUAL_ANNOTATION")
    private String manualAnnotation;

    /**
     * 最终标注结果
     */
    @Column(name = "FINAL_ANNOTATION")
    private String finalAnnotation;

    /**
     * 新术语
     */
    @Column(name = "NEW_TERMS")
    private String newTerms;

    /**
     * @return ID
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取术语ID
     *
     * @return TERM_ID - 术语ID
     */
    public String getTermId() {
        return termId;
    }

    /**
     * 设置术语ID
     *
     * @param termId 术语ID
     */
    public void setTermId(String termId) {
        this.termId = termId;
    }

    /**
     * 获取术语的状态:
    INIT("待人工标注"),
    PROCESSING("标注中"),
    FINISH("标注完成");
     *
     * @return STATE - 术语的状态:
    INIT("待人工标注"),
    PROCESSING("标注中"),
    FINISH("标注完成");
     */
    public String getState() {
        return state;
    }

    /**
     * 设置术语的状态:
    INIT("待人工标注"),
    PROCESSING("标注中"),
    FINISH("标注完成");
     *
     * @param state 术语的状态:
    INIT("待人工标注"),
    PROCESSING("标注中"),
    FINISH("标注完成");
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * 获取修改人
     *
     * @return MODIFIER - 修改人
     */
    public String getModifier() {
        return modifier;
    }

    /**
     * 设置修改人
     *
     * @param modifier 修改人
     */
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    /**
     * 获取创建时间
     *
     * @return GMT_CREATED - 创建时间
     */
    public Date getGmtCreated() {
        return gmtCreated;
    }

    /**
     * 设置创建时间
     *
     * @param gmtCreated 创建时间
     */
    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    /**
     * 获取修改时间
     *
     * @return GMT_MODIFIED - 修改时间
     */
    public Date getGmtModified() {
        return gmtModified;
    }

    /**
     * 设置修改时间
     *
     * @param gmtModified 修改时间
     */
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    /**
     * @return MEMO
     */
    public String getMemo() {
        return memo;
    }

    /**
     * @param memo
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    /**
     * 获取术语内容
     *
     * @return TERM - 术语内容
     */
    public String getTerm() {
        return term;
    }

    /**
     * 设置术语内容
     *
     * @param term 术语内容
     */
    public void setTerm(String term) {
        this.term = term;
    }

    /**
     * 获取自动标注结果
     *
     * @return AUTO_ANNOTATION - 自动标注结果
     */
    public String getAutoAnnotation() {
        return autoAnnotation;
    }

    /**
     * 设置自动标注结果
     *
     * @param autoAnnotation 自动标注结果
     */
    public void setAutoAnnotation(String autoAnnotation) {
        this.autoAnnotation = autoAnnotation;
    }

    /**
     * 获取手工标注结果
     *
     * @return MANUAL_ANNOTATION - 手工标注结果
     */
    public String getManualAnnotation() {
        return manualAnnotation;
    }

    /**
     * 设置手工标注结果
     *
     * @param manualAnnotation 手工标注结果
     */
    public void setManualAnnotation(String manualAnnotation) {
        this.manualAnnotation = manualAnnotation;
    }

    /**
     * 获取最终标注结果
     *
     * @return FINAL_ANNOTATION - 最终标注结果
     */
    public String getFinalAnnotation() {
        return finalAnnotation;
    }

    /**
     * 设置最终标注结果
     *
     * @param finalAnnotation 最终标注结果
     */
    public void setFinalAnnotation(String finalAnnotation) {
        this.finalAnnotation = finalAnnotation;
    }

    /**
     * 获取新术语
     *
     * @return NEW_TERMS - 新术语
     */
    public String getNewTerms() {
        return newTerms;
    }

    /**
     * 设置新术语
     *
     * @param newTerms 新术语
     */
    public void setNewTerms(String newTerms) {
        this.newTerms = newTerms;
    }
}