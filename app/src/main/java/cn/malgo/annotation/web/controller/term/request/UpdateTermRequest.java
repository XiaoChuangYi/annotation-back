package cn.malgo.annotation.web.controller.term.request;

import cn.malgo.annotation.common.util.AssertUtil;


/**
 * Created by cjl on 2017/11/28.
 */
public class UpdateTermRequest {
    private  Integer id;
    private  String  conceptId;
    private  String conceptName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }
    public static void check(UpdateTermRequest request) {
        AssertUtil.notNull(request,"更新单条术语请求对象为空");
        AssertUtil.notBlank(request.getId().toString(),"主键ID为空");
        AssertUtil.notBlank(request.getConceptId(),"termID为空");
        AssertUtil.notBlank(request.getConceptName(),"termName为空");
    }
}
