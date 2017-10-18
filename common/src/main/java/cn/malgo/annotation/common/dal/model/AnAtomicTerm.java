package cn.malgo.annotation.common.dal.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "AN_ATOMIC_TERM")
public class AnAtomicTerm {
    @Id
    @Column(name = "ID")
    private String id;

    /**
     * 术语内容
     */
    @Column(name = "TERM")
    private String term;

    /**
     * 术语类型
     */
    @Column(name = "TYPE")
    private String type;

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
     * 获取术语类型
     *
     * @return TYPE - 术语类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置术语类型
     *
     * @param type 术语类型
     */
    public void setType(String type) {
        this.type = type;
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
}