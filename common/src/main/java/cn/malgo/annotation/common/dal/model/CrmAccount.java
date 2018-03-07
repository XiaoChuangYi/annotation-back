package cn.malgo.annotation.common.dal.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "crm_account")
public class CrmAccount {
    /**
     * 账号ID
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 账号编码
     */
    @Column(name = "account_no")
    private String accountNo;

    /**
     * 登录密码
     */
    @Column(name = "login_pwd")
    private String loginPwd;

    /**
     * 账号状态
     */
    @Column(name = "state")
    private String state;

    /**
     * 创建时间
     */
    @Column(name = "gmt_created")
    private Date gmtCreated;

    /**
     * 修改时间
     */
    @Column(name = "gmt_modified")
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