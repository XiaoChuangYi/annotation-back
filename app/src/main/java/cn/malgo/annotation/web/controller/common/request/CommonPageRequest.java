package cn.malgo.annotation.web.controller.common.request;

import cn.malgo.annotation.web.request.PageRequest;

/**
 *
 * @author 张钟
 * @date 2017/9/26
 */
public class CommonPageRequest extends PageRequest {

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
