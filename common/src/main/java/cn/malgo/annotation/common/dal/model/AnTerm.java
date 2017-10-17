package cn.malgo.annotation.common.dal.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "AN_TERM")
public class AnTerm {
    /**
     * ID
     */
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
     * 0-UNCONFIRMED; 1-CONFIRMED; 2-EXPORTED
     */
    @Column(name = "STATE")
    private String state;

    /**
     * 备注
     */
    @Column(name = "MEMO")
    private String memo;

    @Column(name = "GMT_CREATED")
    private Date gmtCreated;

    @Column(name = "GMT_MODIFIED")
    private Date gmtModified;

    /**
     * 获取ID
     *
     * @return ID - ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置ID
     *
     * @param id ID
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
     * 获取0-UNCONFIRMED; 1-CONFIRMED; 2-EXPORTED
     *
     * @return STATE - 0-UNCONFIRMED; 1-CONFIRMED; 2-EXPORTED
     */
    public String getState() {
        return state;
    }

    /**
     * 设置0-UNCONFIRMED; 1-CONFIRMED; 2-EXPORTED
     *
     * @param state 0-UNCONFIRMED; 1-CONFIRMED; 2-EXPORTED
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * 获取备注
     *
     * @return MEMO - 备注
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 设置备注
     *
     * @param memo 备注
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    /**
     * @return GMT_CREATED
     */
    public Date getGmtCreated() {
        return gmtCreated;
    }

    /**
     * @param gmtCreated
     */
    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    /**
     * @return GMT_MODIFIED
     */
    public Date getGmtModified() {
        return gmtModified;
    }

    /**
     * @param gmtModified
     */
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}