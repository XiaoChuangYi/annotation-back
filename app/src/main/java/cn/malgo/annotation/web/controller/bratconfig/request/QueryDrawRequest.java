package cn.malgo.annotation.web.controller.bratconfig.request;

import cn.malgo.annotation.web.request.PageRequest;

/**
 * Created by cjl on 2017/12/18.
 */
public class QueryDrawRequest extends PageRequest {
    private String type;
    private String color;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
