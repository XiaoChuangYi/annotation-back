package cn.malgo.annotation.web.controller.annotation.request;

import cn.malgo.annotation.common.util.AssertUtil;

/**
 * @author 张钟
 * @date 2017/10/27
 */
public class DeleteNewTermsRequest {

    /** 标注ID **/
    private String anId;

    /** 新词内容 **/
    private String term;

    /** 新词类型 **/
    private String termType;


    public static void check(DeleteNewTermsRequest request){
        AssertUtil.notNull(request,"删除新词请求对象为空");
        AssertUtil.notBlank(request.getAnId(),"标注ID为空");
        AssertUtil.notBlank(request.getTerm(),"新词内容为空");
        AssertUtil.notBlank(request.getTermType(),"新词类型为空");
    }

    public String getAnId() {
        return anId;
    }

    public void setAnId(String anId) {
        this.anId = anId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }
}
