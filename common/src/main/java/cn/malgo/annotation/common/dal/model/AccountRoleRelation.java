package cn.malgo.annotation.common.dal.model;

import javax.persistence.*;

@Table(name = "account_role_relation")
public class AccountRoleRelation {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 用户角色ID
     */
    @Column(name = "userole_id")
    private String useroleId;

    /**
     * 账号ID
     */
    @Column(name = "account_no")
    private String accountNo;

    /**
     * 角色ID
     */
    @Column(name = "role_id")
    private String roleId;

    /**
     * 获取ä¸»é”®ID
     *
     * @return ID - ä¸»é”®ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置ä¸»é”®ID
     *
     * @param id ä¸»é”®ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取ç”¨æˆ·è§’è‰²ID
     *
     * @return USEROLE_ID - ç”¨æˆ·è§’è‰²ID
     */
    public String getUseroleId() {
        return useroleId;
    }

    /**
     * 设置ç”¨æˆ·è§’è‰²ID
     *
     * @param useroleId ç”¨æˆ·è§’è‰²ID
     */
    public void setUseroleId(String useroleId) {
        this.useroleId = useroleId;
    }

    /**
     * 获取ç”¨æˆ·ID
     *
     * @return ACCOUNT_NO - ç”¨æˆ·ID
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * 设置ç”¨æˆ·ID
     *
     * @param accountNo ç”¨æˆ·ID
     */
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    /**
     * 获取è§’è‰²ID
     *
     * @return ROLE_ID - è§’è‰²ID
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * 设置è§’è‰²ID
     *
     * @param roleId è§’è‰²ID
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}