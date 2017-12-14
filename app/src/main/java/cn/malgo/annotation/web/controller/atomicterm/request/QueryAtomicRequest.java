package cn.malgo.annotation.web.controller.atomicterm.request;

import cn.malgo.annotation.web.request.PageRequest;

/**
 * @author 张钟
 * @date 2017/11/1
 */
public class QueryAtomicRequest extends PageRequest {

    /** 原子术语ID **/
    private String id;

    /** 原子术语文本 **/
    private String term;

    /** 原子术语类型 **/
    private String type;

    /** 是否含有同义词**/
    private String checked;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

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
