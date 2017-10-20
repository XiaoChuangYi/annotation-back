package cn.malgo.annotation.web.controller.annotation.result;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author 张钟
 * @date 2017/10/20
 */
public class AnnotationBratVO {

    private String     id;

    private String     state;

    private Date       gmtCreated;

    private Date       gmtModified;

    private JSONObject brat;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public JSONObject getBrat() {
        return brat;
    }

    public void setBrat(JSONObject brat) {
        this.brat = brat;
    }
}
