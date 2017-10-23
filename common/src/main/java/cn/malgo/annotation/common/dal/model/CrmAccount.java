package cn.malgo.annotation.common.dal.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "CRM_ACCOUNT")
public class CrmAccount {
    /**
     * 账号ID
     */
    @Id
    @Column(name = "ID")
    private String id;

    /**
     * 账号编码
     */
    @Column(name = "ACCOUNT_NO")
    private String accountNo;

    /**
     * 登录密码
     */
    @Column(name = "LOGIN_PWD")
    private String loginPwd;

    /**
     * 账号状态
     */
    @Column(name = "STATE")
    private String state;

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
     * 获取账号ID
     *
     * @return ID - 账号ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置账号ID
     *
     * @param id 账号ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取账号编码
     *
     * @return ACCOUNT_NO - 账号编码
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * 设置账号编码
     *
     * @param accountNo 账号编码
     */
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    /**
     * 获取登录密码
     *
     * @return LOGIN_PWD - 登录密码
     */
    public String getLoginPwd() {
        return loginPwd;
    }

    /**
     * 设置登录密码
     *
     * @param loginPwd 登录密码
     */
    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    /**
     * 获取账号状态
     *
     * @return STATE - 账号状态
     */
    public String getState() {
        return state;
    }

    /**
     * 设置账号状态
     *
     * @param state 账号状态
     */
    public void setState(String state) {
        this.state = state;
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