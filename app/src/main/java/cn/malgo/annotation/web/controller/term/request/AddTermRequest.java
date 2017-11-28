package cn.malgo.annotation.web.controller.term.request;


import cn.malgo.annotation.common.util.AssertUtil;

/**
 * Created by cjl on 2017/11/28.
 */
public class AddTermRequest {

    private String conceptId;

    private String pconceptId;

    private String conceptCode;

    private String conceptType;

    private String conceptName;

    private String originName;


    public static void check(AddTermRequest request) {
        AssertUtil.notNull(request,"新增单条术语请求对象为空");
        AssertUtil.notBlank(request.getConceptCode(),"termCode为空");
        AssertUtil.notBlank(request.getConceptId(),"termID为空");
        AssertUtil.notBlank(request.getConceptName(),"termName为空");
        AssertUtil.notBlank(request.getConceptType(),"termType为空");
        AssertUtil.notBlank(request.getPconceptId(),"term父ID为空");
        AssertUtil.notBlank(request.getOriginName(),"原始名称为空");
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }

    public String getPconceptId() {
        return pconceptId;
    }

    public void setPconceptId(String pconceptId) {
        this.pconceptId = pconceptId;
    }

    public String getConceptCode() {
        return conceptCode;
    }

    public void setConceptCode(String conceptCode) {
        this.conceptCode = conceptCode;
    }

    public String getConceptType() {
        return conceptType;
    }

    public void setConceptType(String conceptType) {
        this.conceptType = conceptType;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }
}
