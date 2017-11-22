package cn.malgo.annotation.common.dal.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "CRM_ROLE")
public class CrmRole {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "ID")
    private String id;

    /**
     * 角色ID
     */
    @Column(name = "ROLE_ID")
    private String roleId;

    /**
     * 角色名称
     */
    @Column(name = "ROLE_NAME")
    private String roleName;

    /**
     * 生成时间
     */
    @Column(name = "GMT_CREATED")
    private Date gmtCreated;

    /**
     * 更新时间
     */
    @Column(name = "GMT_UPDATE")
    private Date gmtUpdate;

    /**
     * 获取è‡ªå¢žä¸»é”®
     *
     * @return ID - è‡ªå¢žä¸»é”®
     */
    public String getId() {
        return id;
    }

    /**
     * 设置è‡ªå¢žä¸»é”®
     *
     * @param id è‡ªå¢žä¸»é”®
     */
    public void setId(String id) {
        this.id = id;
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

    /**
     * 获取è§’è‰²åç§°
     *
     * @return ROLE_NAME - è§’è‰²åç§°
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * 设置è§’è‰²åç§°
     *
     * @param roleName è§’è‰²åç§°
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * 获取ç”Ÿæˆæ—¶é—´
     *
     * @return GMT_CREATED - ç”Ÿæˆæ—¶é—´
     */
    public Date getGmtCreated() {
        return gmtCreated;
    }

    /**
     * 设置ç”Ÿæˆæ—¶é—´
     *
     * @param gmtCreated ç”Ÿæˆæ—¶é—´
     */
    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    /**
     * 获取æ›´æ–°æ—¶é—´
     *
     * @return GMT_UPDATE - æ›´æ–°æ—¶é—´
     */
    public Date getGmtUpdate() {
        return gmtUpdate;
    }

    /**
     * 设置æ›´æ–°æ—¶é—´
     *
     * @param gmtUpdate æ›´æ–°æ—¶é—´
     */
    public void setGmtUpdate(Date gmtUpdate) {
        this.gmtUpdate = gmtUpdate;
    }
}