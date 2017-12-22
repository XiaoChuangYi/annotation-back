package cn.malgo.annotation.web.controller.annotation.request;

import cn.malgo.annotation.web.request.PageRequest;

/**
 * Created by cjl on 2017/12/21.
 */
public class QueryDistributionRequest extends PageRequest {
    private String state;
    private String userId;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
