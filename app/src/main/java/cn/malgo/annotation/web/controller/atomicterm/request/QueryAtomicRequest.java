package cn.malgo.annotation.web.controller.atomicterm.request;

import cn.malgo.annotation.web.request.PageRequest;

/**
 * @author 张钟
 * @date 2017/11/1
 */
public class QueryAtomicRequest extends PageRequest {

    /** 原子术语文本 **/
    private String term;

    /** 原子术语类型 **/
    private String type;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
