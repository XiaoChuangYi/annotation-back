package cn.malgo.annotation.web.controller.annotation.request;

import cn.malgo.annotation.web.request.PageRequest;

/**
 * @author 张钟
 * @date 2017/10/31
 */
public class QueryDirectlyRequest extends PageRequest {

    /**
     * 标注状态
     */
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
