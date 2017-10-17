package cn.malgo.annotation.common.dal.model.extend;

import java.util.Date;

/**
 * Created by 张钟 on 2017/9/26.
 */
public class UserCoupon {

    private String  cpName;
    private Integer faceValue;
    private Integer useCondition;
    private String  cpType;
    private String  cpDesc;
    private String  id;
    private String  userId;
    private String  cpId;
    private String  state;
    private Date    startTime;
    private Date    endTime;
    private Date    gmtCreated;
    private Date    gmtModified;

    public String getCpName() {
        return cpName;
    }

    public void setCpName(String cpName) {
        this.cpName = cpName;
    }

    public Integer getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(Integer faceValue) {
        this.faceValue = faceValue;
    }

    public String getCpDesc() {
        return cpDesc;
    }

    public void setCpDesc(String cpDesc) {
        this.cpDesc = cpDesc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCpId() {
        return cpId;
    }

    public void setCpId(String cpId) {
        this.cpId = cpId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public Integer getUseCondition() {
        return useCondition;
    }

    public void setUseCondition(Integer useCondition) {
        this.useCondition = useCondition;
    }

    public String getCpType() {
        return cpType;
    }

    public void setCpType(String cpType) {
        this.cpType = cpType;
    }
}
