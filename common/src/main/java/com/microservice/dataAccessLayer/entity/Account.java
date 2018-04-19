package com.microservice.dataAccessLayer.entity;

import java.util.Date;

/**
 * Created by cjl on 2018/4/11.
 */
public class Account {
    private String id;
    private String accountNo;
    private String loginPwd;
    private String state;
    private Date gmtCreated;
    private Date gmtModified;

    public String getId() {
        return (id==null||id=="")?"11111111111111111111":this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}
